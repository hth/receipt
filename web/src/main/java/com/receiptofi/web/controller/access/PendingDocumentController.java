/**
 *
 */
package com.receiptofi.web.controller.access;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.DocumentPendingService;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileDBService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.form.PendingReceiptForm;
import com.receiptofi.web.form.ReceiptDocumentForm;
import com.receiptofi.web.util.PerformanceProfiling;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
@Controller
@RequestMapping (value = "/access/pendingdocument")
public final class PendingDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(PendingDocumentController.class);

    private String LIST_PENDING_DOCUMENTS = "/pendingdocument";
    private String SHOW_DOCUMENT = "/document";

    @Autowired private DocumentPendingService documentPendingService;
    @Autowired private DocumentUpdateService documentUpdateService;
    @Autowired private FileDBService fileDBService;

    @RequestMapping (method = RequestMethod.GET)
    public ModelAndView loadForm(@ModelAttribute ("pendingReceiptForm") PendingReceiptForm pendingReceiptForm) {

        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int pendingMissingReceipt = 0;
        List<DocumentEntity> pendingDocumentEntityList = documentPendingService.getAllPending(receiptUser.getRid());
        for (DocumentEntity documentEntity : pendingDocumentEntityList) {
            if (documentEntity.getFileSystemEntities() != null) {
                for (FileSystemEntity scaledId : documentEntity.getFileSystemEntities()) {
                    GridFSDBFile gridFSDBFile = fileDBService.getFile(scaledId.getBlobId());
                    String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
                    pendingReceiptForm.addPending(originalFileName, gridFSDBFile.getLength(), documentEntity);
                }
            } else {
                LOG.error("pending document does not contains receipt documentId={}", documentEntity.getId());
                ++pendingMissingReceipt;
            }
        }
        if (pendingMissingReceipt > 0) {
            LOG.error("total pending documents missing receipts count={}", pendingMissingReceipt);
        }

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

        ModelAndView modelAndView = new ModelAndView(LIST_PENDING_DOCUMENTS);
        modelAndView.addObject("pendingReceiptForm", pendingReceiptForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    @RequestMapping (value = "/{documentId}", method = RequestMethod.GET)
    public ModelAndView showDocument(@PathVariable String documentId,
                                     @ModelAttribute ("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm) {

        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DocumentEntity documentEntity = documentUpdateService.findOne(documentId, receiptUser.getRid());
        receiptDocumentForm.setReceiptDocument(documentEntity);

        ModelAndView modelAndView = new ModelAndView(SHOW_DOCUMENT);
        modelAndView.addObject("receiptDocumentForm", receiptDocumentForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    /**
     * Delete operation can only be performed by user and not technician
     *
     * @param receiptDocumentForm
     * @return
     */
    @RequestMapping (value = "/delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute ("receiptDocumentForm") ReceiptDocumentForm receiptDocumentForm) {
        //Check cannot delete a pending receipt which has been processed once, i.e. has receipt id
        //The check here is not required but its better to check before calling service method
        if (StringUtils.isEmpty(receiptDocumentForm.getReceiptDocument().getReceiptId())) {
            switch (receiptDocumentForm.getReceiptDocument().getDocumentStatus()) {
                case TURK_RECEIPT_REJECT:
                    documentUpdateService.deleteRejectedReceiptOCR(receiptDocumentForm.getReceiptDocument());
                    break;
                case TURK_RECEIPT_DUPLICATE:
                    documentUpdateService.deleteRejectedReceiptOCR(receiptDocumentForm.getReceiptDocument());
                    break;
                default:
                    LOG.warn("default condition, delete document={}, documentStatus={} receiptId={}",
                            receiptDocumentForm.getReceiptDocument().getId(),
                            receiptDocumentForm.getReceiptDocument().getDocumentStatus(),
                            receiptDocumentForm.getReceiptDocument().getReceiptId()
                    );
                    documentUpdateService.deletePendingReceiptOCR(receiptDocumentForm.getReceiptDocument());
                    break;
            }
        }
        return "redirect:/access/pendingdocument.htm";
    }
}
