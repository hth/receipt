package com.receiptofi.repository;

import com.mongodb.WriteResult;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;

import java.util.List;

/**
 * JMS Message Manager
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
public interface MessageDocumentManager extends RepositoryManager<MessageDocumentEntity> {

    List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status);

    List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status, int limit);

    List<MessageDocumentEntity> findUpdateWithLimit(String emailId, String receiptUserId, DocumentStatusEnum status);

    List<MessageDocumentEntity> findUpdateWithLimit(String emailId, String receiptUserId, DocumentStatusEnum status, int limit);

    List<MessageDocumentEntity> findAllPending();

    List<MessageDocumentEntity> findPending(String emailId, String receiptUserId, DocumentStatusEnum status);

    WriteResult updateObject(String documentId, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet);

    /**
     * On failure the status is reverted back to PENDING. For now the record is kept locked for the same user.
     * Note: User has to complete all the messages in their queue before logging out of their shift.
     * TODO(hth) May be change the parameters in the future by dropping 'value' parameters as this is currently being defaulted as false in the query
     *
     * @param documentId
     * @param value
     * @return
     */
    WriteResult undoUpdateObject(String documentId, boolean value, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet);

    /**
     * Delete all the messages that are associated with DocumentEntity.
     * Process will include current and previous re-check request messages for the receipt.
     *
     * @param documentId
     */
    void deleteAllForReceiptOCR(String documentId);

    /**
     * Resets document to state before assigning to technician.
     * @param receiptUserId
     */
    void resetDocumentsToInitialState(String receiptUserId);
}
