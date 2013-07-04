package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.types.NotificationTypeEnum;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:29 PM
 */
@Document(collection = "NOTIFICATION")
public class NotificationEntity extends BaseEntity {

    @NotNull
    private String message;

    @NotNull
    private String userProfileId;

    @NotNull
    private boolean notified = false;

    @NotNull
    private NotificationTypeEnum notificationTypeEnum;

    /**
     * Could be a receipt id or receipt ocr id
     */
    private String referenceId;

    private NotificationEntity() {}

    public static NotificationEntity newInstance(NotificationTypeEnum notificationTypeEnum) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationTypeEnum(notificationTypeEnum);
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

    public NotificationTypeEnum getNotificationTypeEnum() {
        return notificationTypeEnum;
    }

    private void setNotificationTypeEnum(NotificationTypeEnum notificationTypeEnum) {
        this.notificationTypeEnum = notificationTypeEnum;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
