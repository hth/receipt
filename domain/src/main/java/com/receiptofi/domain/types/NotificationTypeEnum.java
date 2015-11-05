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

    MESSAGE("MESSAGE", "Message"),
    RECEIPT("RECEIPT", "Receipt"),
    RECEIPT_DELETED("RECEIPT_DELETED", "Receipt Deleted"),
    INVOICE("INVOICE", "Invoice"),
    MILEAGE("MILEAGE", "Mileage"),
    DOCUMENT("DOCUMENT", "Document"),
    EXPENSE_REPORT("EXPENSE_REPORT", "Expense Report"),
    /** DOCUMENT_UPLOAD_FAILED is used in mobile to save local notification when document image fails to upload. */
    DOCUMENT_UPLOAD_FAILED("DOCUMENT_UPLOAD_FAILED", "Document Upload Failed"),
    DOCUMENT_UPLOADED("DOCUMENT_UPLOADED", "Document Uploaded"),
    DOCUMENT_REJECTED("DOCUMENT_REJECTED", "Document Rejected"),
    DOCUMENT_DELETED("DOCUMENT_DELETED", "Document Deleted");

    private final String description;
    private final String name;

    NotificationTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
