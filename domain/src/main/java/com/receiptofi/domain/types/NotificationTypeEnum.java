package com.receiptofi.domain.types;

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

    PUSH_NOTIFICATION("PUSH_NOTIFICATION", "Push Notification", NotificationMarkerEnum.P),
    MESSAGE("MESSAGE", "Message", NotificationMarkerEnum.S),

    EXPENSE_REPORT("EXPENSE_REPORT", "Expense Report", NotificationMarkerEnum.P),
    RECEIPT_DELETED("RECEIPT_DELETED", "Receipt Deleted", NotificationMarkerEnum.S),
    RECEIPT("RECEIPT", "Receipt", NotificationMarkerEnum.P),

    INVOICE("INVOICE", "Invoice", NotificationMarkerEnum.S),

    DOCUMENT("DOCUMENT", "Document", NotificationMarkerEnum.S),
    DOCUMENT_UPLOADED("DOCUMENT_UPLOADED", "Document Uploaded", NotificationMarkerEnum.S),
    DOCUMENT_DELETED("DOCUMENT_DELETED", "Document Deleted", NotificationMarkerEnum.S),
    DOCUMENT_REJECTED("DOCUMENT_REJECTED", "Document Rejected", NotificationMarkerEnum.P),

    /** DOCUMENT_UPLOAD_FAILED is used in mobile to save local notification when document image fails to upload. */
    DOCUMENT_UPLOAD_FAILED("DOCUMENT_UPLOAD_FAILED", "Document Upload Failed", NotificationMarkerEnum.I);

    private final String description;
    private final String name;
    private final NotificationMarkerEnum notificationMarker;

    NotificationTypeEnum(String name, String description, NotificationMarkerEnum notificationMarker) {
        this.name = name;
        this.description = description;
        this.notificationMarker = notificationMarker;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public NotificationMarkerEnum getNotificationMarker() {
        return notificationMarker;
    }

    @Override
    public String toString() {
        return description;
    }
}
