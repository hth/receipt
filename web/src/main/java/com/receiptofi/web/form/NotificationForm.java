package com.receiptofi.web.form;

import com.receiptofi.domain.NotificationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:57 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class NotificationForm {
    private List<NotificationDetailForm> notifications = new ArrayList<>();
    private String count;

    @SuppressWarnings ("unused")
    private NotificationForm(String count, List<NotificationEntity> notifications) {
        this.count = count;
        for (NotificationEntity notification : notifications) {
            NotificationDetailForm notificationDetailForm = NotificationDetailForm.newInstance(notification);
            this.notifications.add(notificationDetailForm);
        }
    }

    public static NotificationForm newInstance(List<NotificationEntity> notifications) {
        return new NotificationForm("", notifications);
    }

    public static NotificationForm newInstance(long count, List<NotificationEntity> notifications) {
        return new NotificationForm(String.valueOf(count), notifications);
    }

    public List<NotificationDetailForm> getNotifications() {
        return this.notifications;
    }

    public String getCount() {
        return count;
    }
}
