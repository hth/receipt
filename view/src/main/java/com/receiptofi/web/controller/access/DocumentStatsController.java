/**
 *
 */
package com.receiptofi.web.controller.access;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.DocumentPendingService;
import com.receiptofi.service.DocumentService;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.PendingReceiptForm;
import com.receiptofi.web.form.ReceiptDocumentForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author hitender
 * @since Jan 6, 2013 4:33:23 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/document")
public class DocumentStatsController {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentStatsController.class);

    @Value ("${PendingDocumentController.listPendingDocuments:/pendingDocument}")
    private String listPendingDocuments;

    @Value ("${PendingDocumentController.listRejectedDocuments:/rejectedDocument}")
    private String listRejectedDocuments;

    @Value ("${PendingDocumentController.showDocument:/document}")
    private String showDocument;

    @Autowired private DocumentPendingService documentPendingService;
    @Autowired private DocumentUpdateService documentUpdateService;
    @Autowired private DocumentService documentService;
    @Autowired private FileDBService fileDBService;

    @RequestMapping (value = "/pending", method = RequestMethod.GET)
    public String getPendingDocuments(
            @ModelAttribute ("pendingReceiptForm")
            PendingReceiptForm pendingReceiptForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int pendingMissingReceipt = 0;
        List<DocumentEntity> pendingDocumentEntityList = documentPendingService.getAllPending(receiptUser.getRid());
        for (DocumentEntity document : pendingDocumentEntityList) {
            if (document.getFileSystemEntities() != null) {
                populatePendingReceiptForm(pendingReceiptForm, document);
            } else {
                LOG.error("pending document does not contains receipt documentId={}", document.getId());
                ++pendingMissingReceipt;
            }
        }
        if (pendingMissingReceipt > 0) {
            LOG.error("total pending documents missing receipts count={}", pendingMissingReceipt);
        }

        return listPendingDocuments;
    }

    private void populatePendingReceiptForm(
            @ModelAttribute ("pendingReceiptForm")
            PendingReceiptForm pendingReceiptForm,

            DocumentEntity document
    ) {
        for (FileSystemEntity scaledId : document.getFileSystemEntities()) {
            switch(document.getDocumentStatus()) {
                case REPROCESS:
                    //TODO(hth) map S3 to get file from cloud as during reprocess file does not exist in system.
                    pendingReceiptForm.addPending(scaledId.getOriginalFilename(), 0, document);
                    break;
                case PENDING:
                    GridFSDBFile gridFSDBFile = fileDBService.getFile(scaledId.getBlobId());
                    String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
                    pendingReceiptForm.addPending(originalFileName, gridFSDBFile.getLength(), document);
                    break;
                default:
                    LOG.error("Reached unreachable condition ", document.getDocumentStatus());
                    throw new UnsupportedOperationException("Reached unreachable condition " + document.getDocumentStatus());
            }
        }
    }

    @RequestMapping (value = "/rejected", method = RequestMethod.GET)
    public ModelAndView getRejectedDocuments(
            @ModelAttribute ("pendingReceiptForm")
            PendingReceiptForm pendingReceiptForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int rejectedMissingReceipt = 0;
        List<DocumentEntity> rejectedDocumentEntityList = documentPendingService.getAllRejected(receiptUser.getRid());
        for (DocumentEntity documentEntity : rejectedDocumentEntityList) {
            if (documentEntity.getFileSystemEntities() != null) {
                for (FileSystemEntity scaledId : documentEntity.getFileSystemEntities()) {
                    GridFSDBFile gridFSDBFile = fileDBService.getFile(scaledId.getBlobId());
                    String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
                    pendingReceiptForm.addRejected(originalFileName, gridFSDBFile.getLength(), documentEntity);
                }
            } else {
                LOG.error("rejected document does not contains receipt documentId={}", documentEntity.getId());
                ++rejectedMissingReceipt;
            }
        }

        if (rejectedMissingReceipt > 0) {
            LOG.error("total rejected documents missing receipts count={}", rejectedMissingReceipt);
        }

        return new ModelAndView(listRejectedDocuments);
    }

    @RequestMapping (value = "/{documentId}", method = RequestMethod.GET)
    public ModelAndView showDocument(
            @PathVariable
            ScrubbedInput documentId,

            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DocumentEntity documentEntity = documentService.findDocumentByRid(documentId.getText(), receiptUser.getRid());
        receiptDocumentForm.setReceiptDocument(documentEntity);

        return new ModelAndView(showDocument);
    }

    /**
     * Delete operation can only be performed by user and not technician.
     *
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping (value = "/delete", method = RequestMethod.POST)
    public String delete(
            @ModelAttribute ("receiptDocumentForm")
            ReceiptDocumentForm receiptDocumentForm
    ) {
        String backTo = "redirect:/access/document/pending.htm";
        //Check cannot delete a pending receipt which has been processed once, i.e. has receipt id
        //The check here is not required but its better to check before calling service method
        if (StringUtils.isEmpty(receiptDocumentForm.getReceiptDocument().getReferenceDocumentId())) {
            switch (receiptDocumentForm.getReceiptDocument().getDocumentStatus()) {
                case REJECT:
                case DUPLICATE:
                    documentUpdateService.deleteRejectedDocument(receiptDocumentForm.getReceiptDocument());
                    backTo = "redirect:/access/document/rejected.htm";
                    break;
                case PENDING:
                    documentUpdateService.deletePendingDocument(receiptDocumentForm.getReceiptDocument());
                    backTo = "redirect:/access/document/pending.htm";
                    break;
                default:
                    LOG.error("default condition, delete document={}, documentStatus={} receiptId={}",
                            receiptDocumentForm.getReceiptDocument().getId(),
                            receiptDocumentForm.getReceiptDocument().getDocumentStatus(),
                            receiptDocumentForm.getReceiptDocument().getReferenceDocumentId()
                    );
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        }
        return backTo;
    }
}
