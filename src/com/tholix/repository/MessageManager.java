package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.MessageReceiptEntityOCR;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
public interface MessageManager extends RepositoryManager<MessageReceiptEntityOCR> {
    static String TABLE = BaseEntity.getClassAnnotationValue(MessageReceiptEntityOCR.class, Document.class, "collection");
    static final int QUERY_LIMIT = 10;

    List<MessageReceiptEntityOCR> findWithLimit();

    List<MessageReceiptEntityOCR> findWithLimit(int limit);

    List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String profileId);

    List<MessageReceiptEntityOCR> findUpdateWithLimit(String emailId, String profileId, int limit);

    List<MessageReceiptEntityOCR> findAllPending();

    List<MessageReceiptEntityOCR> findPending(String emailId, String profileId);

    WriteResult updateObject(String id);

    /**
     * On failure the status is reverted back to OCR_PROCESSED. For now the record is kept locked for the same user.
     * Note: User has to complete all the messages in their queue before logging out of their shift.
     *
     * TODO: May be change the parameters in the future by dropping 'value' parameters as this is currently being defaulted as false in the query
     *
     * @param id
     * @param value
     * @return
     */
    WriteResult undoUpdateObject(String id, boolean value);
}
