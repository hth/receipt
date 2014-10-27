package com.receiptofi.web.form;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.types.NotificationTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * User: hitender
 * Date: 8/17/14 11:37 AM
 */
public class NotificationDetailForm {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationDetailForm.class);

    private static final int OFF_SET = 0;
    private static final int MAX_WIDTH = 43;
    private static final String CLASS = "class='notification'";

    private String referenceId;
    private String message;
    private NotificationTypeEnum notificationType;
    private Date created;

    private NotificationDetailForm(NotificationEntity notification) {
        this.referenceId = notification.getReferenceId();
        this.message = notification.getMessage();
        this.notificationType = notification.getNotificationType();
        this.created = notification.getCreated();
    }

    public static NotificationDetailForm newInstance(NotificationEntity notification) {
        return new NotificationDetailForm(notification);
    }

    public Date getCreated() {
        return created;
    }

    /**
     * Displayed on Landing page
     *
     * @return
     */
    public String getNotificationMessage4Display() {
        switch (notificationType) {
            case MESSAGE:
                return message;
            case DOCUMENT:
                return getReceiptUpdateURL(referenceId, StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            case RECEIPT:
                return getReceiptURL(referenceId, StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            case EXPENSE_REPORT:
                return getReceiptURL(referenceId, StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            case MILEAGE:
                return getMileageURL(referenceId, StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            default:
                LOG.error("Reached invalid condition");
                return "";
        }
    }

    /**
     * Displayed on Notification page
     *
     * @return
     */
    public String getNotificationMessage() {
        switch (notificationType) {
            case MESSAGE:
                return message;
            case DOCUMENT:
                return getReceiptUpdateURL(referenceId, message);
            case RECEIPT:
                return getReceiptURL(referenceId, message);
            case EXPENSE_REPORT:
                return getReceiptURL(referenceId, message);
            case MILEAGE:
                return getMileageURL(referenceId, message);
            default:
                LOG.error("Reached invalid condition");
                return "";
        }
    }


    private String getReceiptUpdateURL(String referenceId, String message) {
        return "<a " + CLASS + " href=\"" + "./pendingdocument/" + referenceId + ".htm" + "\">" + message + "</a>";
    }

    private String getReceiptURL(String referenceId, String message) {
        return "<a class='notification' href=\"" + "./receipt/" + referenceId + ".htm" + "\">" + message + "</a>";
    }

    private String getMileageURL(String referenceId, String message) {
        return "<a class='notification' href=\"" + "./modv/" + referenceId + ".htm" + "\">" + message + "</a>";
    }
}
