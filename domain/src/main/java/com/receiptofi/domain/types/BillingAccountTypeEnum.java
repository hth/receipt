package com.receiptofi.domain.types;

/**
 * Current account status. Based on this, monthly status is computed for billing for each user.
 *
 * User: hitender
 * Date: 3/19/15 2:30 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum BillingAccountTypeEnum {
    UNPAID("UNPAID", "Unpaid"),
    PROMOTION("PROMOTION", "Promotion"),
    MONTHLY("MONTHLY", "Monthly"),
    ANNUAL("ANNUAL", "Annual");

    private final String description;
    private final String name;

    private BillingAccountTypeEnum(String name, String description) {
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
