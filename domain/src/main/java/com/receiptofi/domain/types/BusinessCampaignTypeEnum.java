package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/8/16 10:33 PM
 */
public enum BusinessCampaignTypeEnum {
    L("L", "Promote to your loyal customers"),
    /** Across different business. */
    X("X", "Promote to new customers"),
    A("A", "Promote to all");

    private final String description;
    private final String name;

    BusinessCampaignTypeEnum(String name, String description) {
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
