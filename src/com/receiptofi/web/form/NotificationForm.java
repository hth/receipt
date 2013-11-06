package com.receiptofi.web.form;

import java.util.List;

import com.receiptofi.domain.NotificationEntity;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:57 PM
 */
public final class NotificationForm {

    private List<NotificationEntity> notifications;

    @SuppressWarnings("unused")
    private NotificationForm() {}

    public static NotificationForm newInstance() {
        return new NotificationForm();
    }

    public List<NotificationEntity> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationEntity> notifications) {
        this.notifications = notifications;
    }
}
