package com.tholix.service;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.NotificationEntity;
import com.tholix.domain.types.NotificationTypeEnum;
import com.tholix.repository.NotificationManager;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 2:07 PM
 */
@Service
public class NotificationService {
    private static final Logger log = Logger.getLogger(NotificationService.class);

    @Autowired private NotificationManager notificationManager;

    /**
     * Hide notification from user
     *
     * @param message
     * @param userProfileId
     * @param notified
     */
    public void addNotification(String message, NotificationTypeEnum notificationTypeEnum, String userProfileId, boolean notified) {
        NotificationEntity notificationEntity = NotificationEntity.newInstance(notificationTypeEnum);
        notificationEntity.setMessage(message);
        notificationEntity.setUserProfileId(userProfileId);
        if(notified) {
            notificationEntity.markAsNotified();
        }

        try {
            notificationManager.save(notificationEntity);
        } catch (Exception exce) {
            StringBuilder sb = new StringBuilder()
                    .append("Failed adding notification: ")
                    .append(exce.getLocalizedMessage())
                    .append(", with message: ")
                    .append(message).append(", for user: ")
                    .append(userProfileId);

            log.error(sb.toString());
        }
    }

    /**
     * Show notification to the user
     *
     * @param message
     * @param userProfileId
     */
    public void addNotification(String message, NotificationTypeEnum notificationTypeEnum, String userProfileId) {
        addNotification(message, notificationTypeEnum, userProfileId, true);
    }

    /**
     * List all the notification in descending order
     *
     * @param userProfileId
     * @return
     */
    public List<NotificationEntity> notifications(String userProfileId, int limit) {
        return notificationManager.getAllNotification(userProfileId, limit);
    }

    /**
     * List last five notification in descending order
     *
     * @param userProfileId
     * @return
     */
    public List<NotificationEntity> notifications(String userProfileId) {
        return notifications(userProfileId, NotificationManager.LIMIT_FIVE);
    }
}
