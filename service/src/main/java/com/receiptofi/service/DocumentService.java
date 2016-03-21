package com.receiptofi.service;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemOCRManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 3/20/16 1:47 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class DocumentService {

    private DocumentManager documentManager;
    private ItemOCRManager itemOCRManager;

    @Autowired
    public DocumentService(
            DocumentManager documentManager,
            ItemOCRManager itemOCRManager) {
        this.documentManager = documentManager;
        this.itemOCRManager = itemOCRManager;
    }

    public DocumentEntity loadActiveDocumentById(String documentId) {
        return documentManager.findActiveOne(documentId);
    }

    public DocumentEntity loadRejectedDocumentById(String documentId) {
        return documentManager.findRejectedOne(documentId);
    }

    public DocumentEntity findDocumentByRid(String documentId, String rid) {
        return documentManager.findDocumentByRid(documentId, rid);
    }

    public List<ItemEntityOCR> loadItemsOfReceipt(DocumentEntity receipt) {
        return itemOCRManager.getWhereReceipt(receipt);
    }

    public List<DocumentEntity> getAllProcessedDocuments() {
        return documentManager.getAllProcessedDocuments();
    }

    public List<DocumentEntity> getDocumentsForNotification(int delay) {
        return documentManager.getDocumentsForNotification(delay);
    }

    public void cloudUploadSuccessful(String documentId) {
        documentManager.cloudUploadSuccessful(documentId);
    }

    public void save(DocumentEntity document) {
        documentManager.save(document);
    }

    public void markNotified(String documentId) {
        documentManager.markNotified(documentId);
    }

    public void deleteHard(DocumentEntity document) {
        documentManager.deleteHard(document);
    }
}
