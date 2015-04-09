package com.receiptofi.repository;

import com.receiptofi.domain.NotificationEntity;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
public interface NotificationManager extends RepositoryManager<NotificationEntity> {
    List<NotificationEntity> getNotifications(String rid, int start, int limit);

    long notificationCount(String rid);

    /**
     * Delete notification older than sinceDate.
     *
     * @param sinceDate
     * @return
     */
    int deleteHardInactiveNotification(Date sinceDate);

    /**
     * Set the notification inactive when older than sinceDate.
     *
     * @param sinceDate
     * @return
     */
    int setNotificationInactive(Date sinceDate);
}
