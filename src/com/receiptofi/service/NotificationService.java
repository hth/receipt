package com.receiptofi.service;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.ReceiptEntityOCR;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 2:07 PM
 */
@Service
public final class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired private NotificationManager notificationManager;

    /**
     * Hide notification from user
     *
     * @param message
     * @param notificationTypeEnum
     * @param id
     * @param userProfileId
     * @param notified
     */
    public void addNotification(String message, NotificationTypeEnum notificationTypeEnum, String id, String userProfileId, boolean notified) {
        NotificationEntity notificationEntity = NotificationEntity.newInstance(notificationTypeEnum);
        notificationEntity.setMessage(message);
        notificationEntity.setUserProfileId(userProfileId);
        if(notified) {
            notificationEntity.markAsNotified();
        }
        notificationEntity.setReferenceId(id);

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
        if(notificationTypeEnum == NotificationTypeEnum.MESSAGE) {
            addNotification(message, notificationTypeEnum, null, userProfileId, true);
        } else {
            throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
    }

    /**
     *
     * @param message
     * @param notificationTypeEnum
     * @param receiptEntityOCR
     */
    public void addNotification(String message, NotificationTypeEnum notificationTypeEnum, ReceiptEntityOCR receiptEntityOCR) {
        if(notificationTypeEnum == NotificationTypeEnum.RECEIPT_OCR) {
            addNotification(message, notificationTypeEnum, receiptEntityOCR.getId(), receiptEntityOCR.getUserProfileId(), true);
        } else {
            throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
    }

    /**
     *
     * @param message
     * @param notificationTypeEnum
     * @param receiptEntity
     */
    public void addNotification(String message, NotificationTypeEnum notificationTypeEnum, ReceiptEntity receiptEntity) {
        if(notificationTypeEnum == NotificationTypeEnum.RECEIPT) {
            addNotification(message, notificationTypeEnum, receiptEntity.getId(), receiptEntity.getUserProfileId(), true);
        } else {
            throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
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
