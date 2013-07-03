package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:29 PM
 */
@Document(collection = "NOTIFICATION")
public class NotificationEntity extends BaseEntity {

    //TODO add notification type as this will help in listing of the messages appropriately

    @NotNull
    private String message;

    @NotNull
    private String userProfileId;

    @NotNull
    private boolean notified = false;

    private NotificationEntity() {}

    public static NotificationEntity newInstance() {
        return new NotificationEntity();
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
}
