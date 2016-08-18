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
    N("N", "Notes", 250),
    R("R", "Recheck", 250),
    C("C", "Campaign", 600);

    private final String description;
    private final String name;
    private final int textLength;

    CommentTypeEnum(String name, String description, int textLength) {
        this.name = name;
        this.description = description;
        this.textLength = textLength;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTextLength() {
        return textLength;
    }

    @Override
    public String toString() {
        return description;
    }
}
