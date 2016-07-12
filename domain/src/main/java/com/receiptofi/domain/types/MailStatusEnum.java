package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 7/10/16 6:09 PM
 */
public enum MailStatusEnum {
    N("N", "Not Send"),
    S("S", "Sent"),
    F("F", "Failed");

    private final String description;
    private final String name;

    MailStatusEnum(String name, String description) {
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
