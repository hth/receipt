package com.tholix.domain.types;

/**
 * Used in marking the images are for Receipt or Feedback
 *
 * User: hitender
 * Date: 7/20/13
 * Time: 9:02 PM
 */
public enum FileTypeEnum {
    RECEIPT("RECEIPT", "Receipt"),
    FEEDBACK("FEEDBACK", "Feedback");

    private final String description;
    private final String name;

    private FileTypeEnum(String name, String description) {
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
