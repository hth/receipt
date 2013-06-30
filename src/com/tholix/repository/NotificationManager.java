package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.NotificationEntity;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
public interface NotificationManager extends RepositoryManager<NotificationEntity> {
    static String TABLE = BaseEntity.getClassAnnotationValue(NotificationEntity.class, Document.class, "collection");

}
