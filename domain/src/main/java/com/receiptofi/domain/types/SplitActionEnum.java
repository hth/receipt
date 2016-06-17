package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 9/27/15 1:51 AM
 */
public enum SplitActionEnum {
    A("A", "Add"),
    R("R", "Remove");

    private final String name;
    private final String description;

    SplitActionEnum(String name, String description) {
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
