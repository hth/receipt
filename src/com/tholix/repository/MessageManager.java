package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;

/**
 * JMS Message Manager
 *
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
public interface MessageManager extends RepositoryManager<MessageReceiptEntityOCR> {
    static String TABLE = BaseEntity.getClassAnnotationValue(MessageReceiptEntityOCR.class, Document.class, "collection");
    static final int QUERY_LIMIT = 10;

    List<MessageReceiptEntityOCR> findWithLimit(ReceiptStatusEnum status);

    List<MessageReceiptEntityOCR> findWithLimit(ReceiptStatusEnum status, int limit);

    List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String userProfileId, ReceiptStatusEnum status);

    List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String userProfileId, ReceiptStatusEnum status, int limit);

    List<MessageReceiptEntityOCR> findAllPending();

    List<MessageReceiptEntityOCR> findPending(String emailId, String userProfileId, ReceiptStatusEnum status);

    WriteResult updateObject(String receiptOCRId, ReceiptStatusEnum statusFind, ReceiptStatusEnum statusSet);

    /**
     * On failure the status is reverted back to OCR_PROCESSED. For now the record is kept locked for the same user.
     * Note: User has to complete all the messages in their queue before logging out of their shift.
     *
     * TODO: May be change the parameters in the future by dropping 'value' parameters as this is currently being defaulted as false in the query
     *
     * @param receiptOCRId
     * @param value
     * @return
     */
    WriteResult undoUpdateObject(String receiptOCRId, boolean value, ReceiptStatusEnum statusFind, ReceiptStatusEnum statusSet);

    /**
     * Delete all the messages that are associated with ReceiptEntityOCR.
     * Process will include current and previous re-check request messages for the receipt
     *
     * @param receiptOCRId
     */
    void deleteAllForReceiptOCR(String receiptOCRId);
}
