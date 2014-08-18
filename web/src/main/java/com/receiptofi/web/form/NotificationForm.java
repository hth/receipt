package com.receiptofi.web.form;

import com.receiptofi.domain.NotificationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:57 PM
 */
public final class NotificationForm {
    private static final Logger log = LoggerFactory.getLogger(NotificationForm.class);

    private List<NotificationDetailForm> notifications = new ArrayList<>();

    @SuppressWarnings("unused")
    private NotificationForm(List<NotificationEntity> notifications) {
        for(NotificationEntity notification : notifications) {
            NotificationDetailForm notificationDetailForm = NotificationDetailForm.newInstance(notification);
            this.notifications.add(notificationDetailForm);
        }
    }

    public static NotificationForm newInstance(List<NotificationEntity> notifications) {
        return new NotificationForm(notifications);
    }

    public List<NotificationDetailForm> getNotifications() {
        return this.notifications;
    }
}
