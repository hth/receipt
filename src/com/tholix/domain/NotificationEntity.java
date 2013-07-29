package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.tholix.domain.types.NotificationTypeEnum;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:29 PM
 */
@Document(collection = "NOTIFICATION")
public class NotificationEntity extends BaseEntity {
    private static final int OFF_SET = 0;
    private static final int MAX_WIDTH = 45;

    @NotNull
    @Field("MESSAGE")
    private String message;

    @NotNull
    @Field("USER_PROFILE_ID")
    private String userProfileId;

    @NotNull
    @Field("NOTIFIED")
    private boolean notified = false;

    @NotNull
    @Field("NOTIFICATION_ENUM")
    private NotificationTypeEnum notificationType;

    /**
     * Could be a receipt id or receipt ocr id
     */
    private String referenceId;

    private NotificationEntity() {}

    public static NotificationEntity newInstance(NotificationTypeEnum notificationType) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationType(notificationType);
        return notificationEntity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public boolean isNotified() {
        return notified;
    }

    public void markAsNotified() {
        setNotified(true);
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    private void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * Move this to form if exists
     *
     * @return
     */
    public String getNotificationMessage4Display() {
        switch(notificationType) {
            case MESSAGE:
                return getMessage();
            case RECEIPT_OCR:
                return "<a href=\"" + "./emp/update.htm?id=" + getReferenceId() + "\">" + getMessage4Display() + "</a>";
            case RECEIPT:
                return "<a href=\"" + "./receipt.htm?id=" + getReferenceId() + "\">" + getMessage4Display() + "</a>";
            default:
                throw new UnsupportedOperationException("Reached invalid condition in Notification");
        }
    }

    public String getNotificationMessage() {
        switch(notificationType) {
            case MESSAGE:
                return getMessage();
            case RECEIPT_OCR:
                return "<a href=\"" + "./emp/update.htm?id=" + getReferenceId() + "\">" + getMessage() + "</a>";
            case RECEIPT:
                return "<a href=\"" + "./receipt.htm?id=" + getReferenceId() + "\">" + getMessage() + "</a>";
            default:
                throw new UnsupportedOperationException("Reached invalid condition in Notification");
        }
    }

    /**
     * Used in displaying the message in short form on landing page
     *
     * @return
     */
    @Transient
    private String getMessage4Display() {
        return StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH);
    }
}
