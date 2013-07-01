package com.tholix.service;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.NotificationEntity;
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

    public void addNotification(String message, String userProfileId) {
        NotificationEntity notificationEntity = NotificationEntity.newInstance();
        notificationEntity.setMessage(message);
        notificationEntity.setUserProfileId(userProfileId);

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
     * List all the notification in descending order
     *
     * @param userProfileId
     * @return
     */
    public List<NotificationEntity> notifications(String userProfileId) {
        return notificationManager.getAllNotification(userProfileId, NotificationManager.LIMIT_FIVE);
    }
}
