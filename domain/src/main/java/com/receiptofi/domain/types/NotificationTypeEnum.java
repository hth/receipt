package com.receiptofi.domain.types;

import static com.receiptofi.domain.types.NotificationMarkerEnum.I;
import static com.receiptofi.domain.types.NotificationMarkerEnum.P;
import static com.receiptofi.domain.types.NotificationMarkerEnum.S;

/**
 * User: hitender
 * Date: 7/3/13
 * Time: 8:20 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum NotificationTypeEnum {

    PUSH_NOTIFICATION("PUSH_NOTIFICATION", "Push Notification", P, 1),
    MESSAGE("MESSAGE", "Message", S, 100),

    EXPENSE_REPORT("EXPENSE_REPORT", "Expense Report", P, 4),
    RECEIPT_DELETED("RECEIPT_DELETED", "Receipt Deleted", S, 100),
    RECEIPT("RECEIPT", "Receipt", P, 4),

    INVOICE("INVOICE", "Invoice", S, 100),

    DOCUMENT("DOCUMENT", "Document", S, 100),
    DOCUMENT_UPLOADED("DOCUMENT_UPLOADED", "Document Uploaded", S, 100),
    DOCUMENT_DELETED("DOCUMENT_DELETED", "Document Deleted", S, 100),
    DOCUMENT_REJECTED("DOCUMENT_REJECTED", "Document Rejected", P, 1),

    /** DOCUMENT_UPLOAD_FAILED is used in mobile to save local notification when document image fails to upload. */
    DOCUMENT_UPLOAD_FAILED("DOCUMENT_UPLOAD_FAILED", "Document Upload Failed", I, 100);

    public final String description;
    public final String name;
    public final NotificationMarkerEnum notificationMarker;
    public final int delayNotifying;

    /**
     * @param name
     * @param description
     * @param notificationMarker
     * @param delayNotifying Delay notifying by number of minutes.
     */
    NotificationTypeEnum(String name, String description, NotificationMarkerEnum notificationMarker, int delayNotifying) {
        this.name = name;
        this.description = description;
        this.notificationMarker = notificationMarker;
        this.delayNotifying = delayNotifying;
    }

    @Override
    public String toString() {
        return description;
    }
}
