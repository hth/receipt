package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/28/16 4:27 PM
 */
public enum CouponUploadStatusEnum {
    I("I", "Ignore Upload"),
    A("A", "Awaiting Upload"),
    C("C", "Upload Complete");

    private String name;
    private String description;

    CouponUploadStatusEnum(String name, String description) {
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
