package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 7/23/13
 * Time: 6:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum CommentTypeEnum {
    N("N", "Notes"),
    R("R", "Recheck"),
    C("C", "Coupon");

    private final String description;
    private final String name;

    CommentTypeEnum(String name, String description) {
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
