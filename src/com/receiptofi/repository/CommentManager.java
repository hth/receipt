package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CommentEntity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 7:12 PM
 */
public interface CommentManager extends RepositoryManager<CommentEntity> {
    String TABLE = BaseEntity.getClassAnnotationValue(CommentEntity.class, Document.class, "collection");
}
