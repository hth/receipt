package com.tholix.domain.types;

/**
 * User: hitender
 * Date: 7/3/13
 * Time: 8:20 PM
 */
public enum NotificationTypeEnum {

    MESSAGE("MESSAGE",          "Message"),
    RECEIPT("RECEIPT",          "Receipt"),
    RECEIPT_OCR("RECEIPT_OCR",  "Receipt OCR");

    private final String description;
    private final String name;

    private NotificationTypeEnum(String name, String description) {
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
