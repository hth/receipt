/**
 *
 */
package com.tholix.domain.types;

/**
 * @author hitender
 * @since Jan 5, 2013 7:37:02 PM
 */
public enum ReceiptStatusEnum {

    /** Note: Do not change the order. New ENUM should be appended at the bottom */
    OCR_PROCESSED("PROCESSED",          "OCR Processed"),
    TURK_PROCESSED("TURK_PROCESSED",    "Turk Processed"),
    TURK_REQUEST("TURK_REQUEST",        "Turk Request");

    private final String description;
    private final String name;

    private ReceiptStatusEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getValue() {
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
