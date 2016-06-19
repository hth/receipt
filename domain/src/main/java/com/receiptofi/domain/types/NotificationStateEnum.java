package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 1/22/16 2:15 PM
 */
public enum NotificationStateEnum {
    S("S", "Success"),
    F("F", "Failure");

    private final String description;
    private final String name;

    NotificationStateEnum(String name, String description) {
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
        return description;
    }
}
