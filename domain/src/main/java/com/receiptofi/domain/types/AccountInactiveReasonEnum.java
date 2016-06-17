package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 3/7/15 7:33 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum AccountInactiveReasonEnum {
    ANV("ACCOUNT_NOT_VALIDATED", "Account Not Validated");

    private final String name;
    private final String description;

    AccountInactiveReasonEnum(String name, String description) {
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
