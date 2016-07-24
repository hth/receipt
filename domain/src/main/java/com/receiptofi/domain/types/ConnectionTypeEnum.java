package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 9/18/15 9:51 AM
 */
public enum ConnectionTypeEnum {
    A("A", "Accept"),
    D("D", "Decline"),
    C("C", "Cancel"),
    R("R", "Revoke");

    private final String description;
    private final String name;

    ConnectionTypeEnum(String name, String description) {
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
