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
    INVOICE("INVOICE", "Invoice"),
    MILEAGE("MILEAGE", "Mileage"),
    DOCUMENT("DOCUMENT", "Document"),
    EXPENSE_REPORT("EXPENSE_REPORT", "Expense Report"),
    DOCUMENT_UPLOADED("DOCUMENT_UPLOADED", "Document Uploaded"),
    DOCUMENT_REJECTED("DOCUMENT_REJECTED", "Document Rejected");

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
