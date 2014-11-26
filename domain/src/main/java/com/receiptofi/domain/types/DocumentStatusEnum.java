/**
 *
 */
package com.receiptofi.domain.types;

/**
 * @author hitender
 * @since Jan 5, 2013 7:37:02 PM
 */
public enum DocumentStatusEnum {

    PENDING("Pending"),
    PROCESSED("Processed"),
    REPROCESS("Reprocess"),

    /** Reject condition when its hard to process a receipts because of the image quality or invalid image */
    REJECT("Reject"),

    /** Reject when system suggests document is duplicate */
    DUPLICATE("Duplicate");

    private final String description;

    private DocumentStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
