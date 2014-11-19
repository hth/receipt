package com.receiptofi.repository;

import java.util.List;

import com.receiptofi.domain.NotificationEntity;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
public interface NotificationManager extends RepositoryManager<NotificationEntity> {
    List<NotificationEntity> getNotifications(String receiptUserId, int start, int limit);

    long notificationCount(String receiptUserId);
}
