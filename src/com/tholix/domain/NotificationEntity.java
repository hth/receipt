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

    @NotNull
    private String message;

    @NotNull
    private String userProfileId;

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
}
