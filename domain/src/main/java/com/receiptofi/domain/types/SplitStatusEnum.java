package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 10/5/15 10:28 AM
 */
public enum SplitStatusEnum {
    U("UNSETTLE", "Un-Settled"),
    S("SETTLED", "Settled");

    private final String description;
    private final String name;

    SplitStatusEnum(String name, String description) {
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
