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
    PROMOTION("PROMOTION", "Promotion"),
    NO_BILLING("NO_BILLING", "No Billing"),
    MONTHLY_30("MONTHLY_30", "Monthly 30"),
    MONTHLY_40("MONTHLY_40", "Monthly 40"),
    MONTHLY_50("MONTHLY_50", "Monthly 50"),
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
