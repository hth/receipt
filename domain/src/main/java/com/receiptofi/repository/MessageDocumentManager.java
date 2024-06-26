package com.receiptofi.repository;

import com.mongodb.WriteResult;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;

import java.util.Date;
import java.util.List;

/**
 * JMS Message Manager
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
public interface MessageDocumentManager extends RepositoryManager<MessageDocumentEntity> {

    /**
     * Find messages that are before the delayed time with specified document status.
     *
     * @param email
     * @param rid
     * @param status
     * @return
     */
    List<MessageDocumentEntity> findUpdateWithLimit(String email, String rid, DocumentStatusEnum status);

    List<MessageDocumentEntity> findAllPending(Date since);

    List<MessageDocumentEntity> findPending(String email, String rid, DocumentStatusEnum status);

    WriteResult updateObject(String did, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet);

    /**
     * On failure the status is reverted back to PENDING. For now the record is kept locked for the same user.
     * Note: User has to complete all the messages in their queue before logging out of their shift.
     * TODO(hth) May be change the parameters in the future by dropping 'value' parameters as this is currently being defaulted as false in the query
     *
     * @param did
     * @param value
     * @return
     */
    WriteResult undoUpdateObject(String did, boolean value, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet);

    /**
     * Delete all the messages that are associated with DocumentEntity.
     * Process will include current and previous re-check request messages for the receipt.
     *
     * @param did
     */
    int deleteAllForReceiptOCR(String did);

    /**
     * Resets document to state before assigning to technician.
     *
     * @param receiptUserId
     */
    void resetDocumentsToInitialState(String receiptUserId);

    /**
     * Used for auto rejecting when find existing document with similar name.
     *
     * @param did
     * @param email
     * @param rid
     */
    boolean lockMessageWhenDuplicate(String did, String email, String rid);
}
