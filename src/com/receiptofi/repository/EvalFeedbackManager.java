package com.receiptofi.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.EvalFeedbackEntity;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:37 PM
 */
public interface EvalFeedbackManager extends RepositoryManager<EvalFeedbackEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(EvalFeedbackEntity.class, Document.class, "collection");
}
