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
public enum AccountBillingTypeEnum {
    P("P", "Promotion"),
    NB("NB", "No Billing"),
    M10("M10", "Monthly 10"),
    M30("M30", "Monthly 30"),
    M40("M40", "Monthly 40"),
    M50("M50", "Monthly 50"),
    A("A", "Annual");

    private final String description;
    private final String name;

    private AccountBillingTypeEnum(String name, String description) {
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
