package com.receiptofi.domain.types;

/**
 * Set billing status for each user for each month.
 *
 * User: hitender
 * Date: 3/19/15 1:03 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum BilledStatusEnum {
    UNPAID("UNPAID", "Unpaid"),
    PROMOTION("PROMOTION", "Promotion"),
    PAID("PAID", "Paid");

    private final String description;
    private final String name;

    private BilledStatusEnum(String name, String description) {
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
