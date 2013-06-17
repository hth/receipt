package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.CommentEntity;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 7:12 PM
 */
public interface CommentManager extends RepositoryManager<CommentEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(CommentEntity.class, Document.class, "collection");
}
