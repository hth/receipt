package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.NotificationEntity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
public interface NotificationManager extends RepositoryManager<NotificationEntity> {
    static String TABLE = BaseEntity.getClassAnnotationValue(NotificationEntity.class, Document.class, "collection");
    static int LIMIT_FIVE = 5;
    static int ALL = -1;

    /**
     * List all the notification that are meant to be shown to the user
     *
     * @param userProfileId
     * @param limit
     * @return
     */
    List<NotificationEntity> getAllNotification(String userProfileId, int limit);

}
