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

    public List<NotificationDetailForm> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(List<NotificationEntity> notifications) {
        for (NotificationEntity notification : notifications) {
            NotificationDetailForm notificationDetailForm = NotificationDetailForm.newInstance(notification);
            this.notifications.add(notificationDetailForm);
        }
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
