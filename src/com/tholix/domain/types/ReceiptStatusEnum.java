/**
 *
 */
package com.tholix.domain.types;

/**
 * @author hitender
 * @when Jan 5, 2013 7:37:02 PM
 */
public enum ReceiptStatusEnum {

    OCR_PROCESSED("PROCESSED",          "OCR Processed"),
    TURK_REQUEST("TURK_REQUEST",        "Turk Request"),
    TURK_PROCESSED("TURK_PROCESSED",    "Turk Processed");

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
