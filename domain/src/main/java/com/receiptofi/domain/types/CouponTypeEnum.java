package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 5/9/16 12:11 AM
 */
public enum CouponTypeEnum {

    I("I", "Individual"),
    B("B", "Business");

    private String name;
    private String description;

    CouponTypeEnum(String name, String description) {
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
