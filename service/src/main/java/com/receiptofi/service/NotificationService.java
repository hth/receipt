package com.receiptofi.service;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationMarkerEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.PaginationEnum;
import com.receiptofi.repository.NotificationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    private NotificationManager notificationManager;

    @Autowired
    public NotificationService(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    /**
     * Hide notification from user.
     *
     * @param message
     * @param notificationType
     * @param id
     * @param rid
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            NotificationGroupEnum notificationGroup,
            String id,
            String rid
    ) {
        NotificationEntity notification = NotificationEntity.newInstance(notificationType);
        notification.setMessage(message);
        notification.setReceiptUserId(rid);
        notification.setNotificationMarkerEnum(notificationType.getNotificationMarker());
        notification.setNotificationGroup(notificationGroup);
        if (notificationType.getNotificationMarker() != NotificationMarkerEnum.P) {
            /** Defaults to success as its not going to be sent through Push Notification. */
            notification.setNotificationStateToSuccess();
        }
        notification.setReferenceId(id);

        try {
            notificationManager.save(notification);
        } catch (Exception exce) {
            LOG.error("Failed adding notification={}, with message={}, for user={}",
                    exce.getLocalizedMessage(),
                    message,
                    rid);
        }
    }

    /**
     * Show notification to the user.
     *
     * @param message
     * @param notificationType  either MESSAGE or PUSH_NOTIFICATION
     * @param notificationGroup to group notification in types for picking right icons
     * @param rid
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            NotificationGroupEnum notificationGroup,
            String rid
    ) {
        switch (notificationType) {
            case PUSH_NOTIFICATION:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        null,
                        rid);
                break;
            case MESSAGE:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        null,
                        rid);
                break;
            default:
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
            NotificationGroupEnum notificationGroup,
            BaseEntity supportedEntity
    ) {
        switch (notificationType) {
            case EXPENSE_REPORT:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId());
                break;
            case RECEIPT_DELETED:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId());
                break;
            case RECEIPT:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId());
                break;
            case INVOICE:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((ReceiptEntity) supportedEntity).getReceiptUserId());
                break;
            case MILEAGE:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((MileageEntity) supportedEntity).getReceiptUserId());
                break;
            case DOCUMENT:
            case DOCUMENT_UPLOADED:
            case DOCUMENT_DELETED:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((DocumentEntity) supportedEntity).getReceiptUserId());
                break;
            case DOCUMENT_REJECTED:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        supportedEntity.getId(),
                        ((DocumentEntity) supportedEntity).getReceiptUserId());
                break;
            case DOCUMENT_UPLOAD_FAILED:
                LOG.error("Not supported Notification Type: {}", notificationType);
                throw new UnsupportedOperationException("Not supported Notification Type: " + notificationType);
            default:
                throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
    }

    /**
     * List all the notification in descending order.
     *
     * @param rid
     * @return
     */
    public List<NotificationEntity> getAllNotifications(String rid) {
        return getNotifications(rid, PaginationEnum.ALL.getLimit());
    }

    private List<NotificationEntity> getNotifications(String rid, int limit) {
        return notificationManager.getNotifications(rid, 0, limit);
    }

    /**
     * List last five notification in descending order.
     *
     * @param rid
     * @return
     */
    public List<NotificationEntity> getNotifications(String rid) {
        return getNotifications(rid, PaginationEnum.FIVE.getLimit());
    }

    /**
     * List last five notification in descending order.
     *
     * @param rid
     * @return
     */
    public List<NotificationEntity> notificationsPaginated(String rid, int start) {
        return notificationManager.getNotifications(rid, start, PaginationEnum.FIVE.getLimit());
    }

    public long notificationCount(String rid) {
        return notificationManager.notificationCount(rid);
    }

    public int deleteInactiveNotification(Date sinceDate) {
        return notificationManager.deleteHardInactiveNotification(sinceDate);
    }

    public int setNotificationInactive(Date sinceDate) {
        return notificationManager.setNotificationInactive(sinceDate);
    }

    String getNotificationMessageForReceiptProcess(ReceiptEntity receipt, String action) {
        return receipt.getTotalString() +
                " '" +
                receipt.getBizName().getBusinessName() +
                "' receipt " +
                action;
    }
}
