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
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class NotificationDetailForm {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationDetailForm.class);

    private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM. dd");
    private static final int OFF_SET = 0;
    private static final int MAX_WIDTH = 50;  // or 70
    private static final int MAX_WIDTH_DETAILED_VIEW = 78;
    private static final String CLASS = "class='rightside-li-middle-text full-li-middle-text'";

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
        return SDF.format(created);
    }

    /**
     * Displayed on Landing page.
     *
     * @return
     */
    public String getNotificationMessageForDisplay() {
        switch (notificationType) {
            case PUSH_NOTIFICATION:
            case MESSAGE:
            case DOCUMENT_DELETED:
            case RECEIPT_DELETED:
                return getAbbreviatedMessage();
            case DOCUMENT:
            case DOCUMENT_UPLOADED:
            case DOCUMENT_REJECTED:
                this.href = "./document/" + referenceId + ".htm";
                return getReceiptUpdateURL(getAbbreviatedMessage());
            case RECEIPT:
                this.href = "./receipt/" + referenceId + ".htm";
                return getReceiptURL(getAbbreviatedMessage());
            case EXPENSE_REPORT:
                this.href = "./receipt/" + referenceId + ".htm";
                return getReceiptURL(getAbbreviatedMessage());
            case MILEAGE:
                this.href = "./modv/" + referenceId + ".htm";
                return getMileageURL(getAbbreviatedMessage());
            default:
                LOG.error("Reached invalid condition");
                return "";
        }
    }

    /**
     * Displayed on Notification page.
     *
     * @return
     */
    @SuppressWarnings ("unused")
    public String getNotificationMessage() {
        switch (notificationType) {
            case PUSH_NOTIFICATION:
            case MESSAGE:
            case DOCUMENT_DELETED:
            case RECEIPT_DELETED:
                return "<span " + CLASS + ">" + message + "</span>";
            case DOCUMENT:
            case DOCUMENT_UPLOADED:
            case DOCUMENT_REJECTED:
                this.href = "./document/" + referenceId + ".htm";
                return getReceiptUpdateURL(getAbbreviatedMessageForDetailedView());
            case RECEIPT:
                this.href = "./receipt/" + referenceId + ".htm";
                return getReceiptURL(getAbbreviatedMessageForDetailedView());
            case EXPENSE_REPORT:
                this.href = "./receipt/" + referenceId + ".htm";
                return getReceiptURL(getAbbreviatedMessageForDetailedView());
            case MILEAGE:
                this.href = "./modv/" + referenceId + ".htm";
                return getMileageURL(getAbbreviatedMessageForDetailedView());
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

    public String getAbbreviatedMessageForDetailedView() {
        return StringUtils.abbreviate(message, OFF_SET, MAX_WIDTH_DETAILED_VIEW);
    }
}
