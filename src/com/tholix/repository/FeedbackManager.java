package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.FeedbackEntity;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:37 PM
 */
public interface FeedbackManager extends RepositoryManager<FeedbackEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(FeedbackEntity.class, Document.class, "collection");
}
