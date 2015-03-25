package com.receiptofi.service;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.PaginationEnum;
import com.receiptofi.repository.NotificationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 2:07 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    @Autowired private NotificationManager notificationManager;

    /**
     * Hide notification from user.
     *
     * @param message
     * @param notificationType
     * @param id
     * @param userProfileId
     * @param notified
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            String id,
            String userProfileId,
            boolean notified
    ) {
        NotificationEntity notificationEntity = NotificationEntity.newInstance(notificationType);
        notificationEntity.setMessage(message);
        notificationEntity.setReceiptUserId(userProfileId);
        if (notified) {
            notificationEntity.markAsNotified();
        }
        notificationEntity.setReferenceId(id);

        try {
            notificationManager.save(notificationEntity);
        } catch (Exception exce) {
            LOG.error("Failed adding notification={}, with message={}, for user={}",
                    exce.getLocalizedMessage(),
                    message,
                    userProfileId);
        }
    }

    /**
     * Show notification to the user.
     *
     * @param message
     * @param userProfileId
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            String userProfileId
    ) {
        if (notificationType == NotificationTypeEnum.MESSAGE) {
            addNotification(message, notificationType, null, userProfileId, true);
        } else {
            throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
    }

    /**
     * @param message
     * @param notificationType
     * @param supportedEntity
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            BaseEntity supportedEntity
    ) {
        switch (notificationType) {
            case EXPENSE_REPORT:
                addNotification(
                        message,
                        notificationType,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId(),
                        true);
                break;
            case RECEIPT:
                addNotification(
                        message,
                        notificationType,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId(),
                        true);
                break;
            case INVOICE:
                addNotification(
                        message,
                        notificationType,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId(),
                        true);
                break;
            case MILEAGE:
                addNotification(
                        message,
                        notificationType,
                        supportedEntity.getId(),
                        ((MileageEntity) supportedEntity).getReceiptUserId(),
                        true);
                break;
            case DOCUMENT:
                addNotification(
                        message,
                        notificationType,
                        supportedEntity.getId(),
                        ((DocumentEntity) supportedEntity).getReceiptUserId(),
                        true);
                break;
            default:
                throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
    }

    /**
     * List all the notification in descending order.
     *
     * @param userProfileId
     * @return
     */
    public List<NotificationEntity> getAllNotifications(String userProfileId, int limit) {
        return notificationManager.getNotifications(userProfileId, 0, limit);
    }

    /**
     * List last five notification in descending order.
     *
     * @param userProfileId
     * @return
     */
    public List<NotificationEntity> notifications(String userProfileId) {
        return getAllNotifications(userProfileId, PaginationEnum.FIVE.getLimit());
    }

    /**
     * List last five notification in descending order.
     *
     * @param userProfileId
     * @return
     */
    public List<NotificationEntity> notificationsPaginated(String userProfileId, int start) {
        return notificationManager.getNotifications(userProfileId, start, PaginationEnum.FIVE.getLimit());
    }

    public long notificationCount(String userProfileId) {
        return notificationManager.notificationCount(userProfileId);
    }
}
