package com.tholix.service.routes;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemFeatureEntity;
import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.service.RepositoryManager;

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

    WriteResult updateObject(String id);

    WriteResult updateObject(String id, boolean value);
}
