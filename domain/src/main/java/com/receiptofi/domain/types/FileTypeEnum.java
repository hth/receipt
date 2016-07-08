package com.receiptofi.domain.types;

/**
 * Used in marking the images are for Receipt or Feedback.
 * User: hitender
 * Date: 7/20/13
 * Time: 9:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum FileTypeEnum {
    /** Receipt ends up on cloud. */
    R("R", "Receipt"),

    /** Coupon ends up on cloud. */
    C("C", "Coupon"),

    I("I", "Invoice"),

    /** File stays local. */
    F("F", "Feedback"),

    /** File stays local. */
    D("D", "Document");

    private final String description;
    private final String name;

    FileTypeEnum(String name, String description) {
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
