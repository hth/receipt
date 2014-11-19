package com.receiptofi.repository;

import com.receiptofi.domain.NotificationEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
public interface NotificationManager extends RepositoryManager<NotificationEntity> {
    List<NotificationEntity> getNotifications(String receiptUserId, int start, int limit);

    long notificationCount(String receiptUserId);
}
