package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 2/29/16 1:50 PM
 */
public enum NotificationGroupEnum {
    /** Friendship request, connection, invitation send) */
    S("S", "Social"),

    /** File/Document delete, File/Document upload, ... */
    F("F", "File"),

    /** Receipt notifications(receipt processed) */
    R("R", "Receipt"),

    /** For welcome message or other random un-associated messages. */
    N("N", "Normal Message");

    private String name;
    private String description;

    NotificationGroupEnum(String name, String description) {
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
