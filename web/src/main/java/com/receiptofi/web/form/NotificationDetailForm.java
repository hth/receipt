package com.receiptofi.web.form;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.types.NotificationTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: hitender
 * Date: 8/17/14 11:37 AM
 */
public class NotificationDetailForm {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationDetailForm.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM. dd");
    private static final int OFF_SET = 0;
    private static final int MAX_WIDTH = 53;
    private static final String CLASS = "class='notification'";

    private String referenceId;
    private String href;
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

    public String getCreatedStr() {
        return sdf.format(created);
    }

    /**
     * Displayed on Landing page
     *
     * @return
     */
    public String getNotificationMessageForDisplay() {
        switch (notificationType) {
            case MESSAGE:
                return message;
            case DOCUMENT:
                this.href = "./pendingdocument/" + referenceId + ".htm";
                return getReceiptUpdateURL(StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            case RECEIPT:
                this.href = "./receipt/" + referenceId + ".htm";
                return getReceiptURL(StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            case EXPENSE_REPORT:
                this.href = "./receipt/" + referenceId + ".htm";
                return getReceiptURL(StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
            case MILEAGE:
                this.href = "./modv/" + referenceId + ".htm";
                return getMileageURL(StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH));
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
                return getReceiptUpdateURL(message);
            case RECEIPT:
                return getReceiptURL(message);
            case EXPENSE_REPORT:
                return getReceiptURL(message);
            case MILEAGE:
                return getMileageURL(message);
            default:
                LOG.error("Reached invalid condition");
                return "";
        }
    }


    private String getReceiptUpdateURL(String message) {
        return "<a " + CLASS + " href=\"" + href + "\">" + message + "</a>";
    }

    private String getReceiptURL(String message) {
        return "<a " + CLASS + " href=\"" + href + "\">" + message + "</a>";
    }

    private String getMileageURL(String message) {
        return "<a " + CLASS + " href=\"" + href + "\">" + message + "</a>";
    }

    public String getHref() {
        return href;
    }

    public String getAbbreviatedMessage() {
        return StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH);
    }
}
