package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 11/19/14 11:06 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum PaginationEnum {
    ALL(-1),
    FIVE(5),
    TEN(10);

    private int limit;

    PaginationEnum(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
