/**
 *
 */
package com.receiptofi.domain.types;

/**
 * @author hitender
 * @since Jan 5, 2013 7:37:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum DocumentStatusEnum {

    PENDING("Pending"),
    PROCESSED("Processed"),
    REPROCESS("Reprocess"),

    /**
     * Reject condition when its hard to process a receipts because of the image quality or invalid image
     * @see DocumentRejectReasonEnum
     */
    REJECT("Reject"),

    /** Reject when system suggests document is duplicate. */
    DUPLICATE("Duplicate");

    private final String description;

    DocumentStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
