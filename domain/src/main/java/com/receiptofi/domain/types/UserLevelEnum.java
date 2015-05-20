/**
 *
 */
package com.receiptofi.domain.types;

/**
 * User level defines the roles set for a user in UserAccountEntity. Roles in UserAccountEntity sets authorities in
 * ReceiptUser.
 *
 * @see com.receiptofi.domain.UserAccountEntity
 * @see com.receiptofi.domain.site.ReceiptUser
 * @see com.receiptofi.domain.types.RoleEnum
 *
 * @author hitender
 * @since Mar 25, 2013 1:11:21 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum UserLevelEnum {
    USER("User", 10),
    USER_COMMUNITY("User Community", 20),
    USER_PAID("User Paid", 20),
    EMPLOYER("Employer", 30),
    EMPLOYER_COMMUNITY("Employer Community", 40),
    EMPLOYER_PAID("Employer Paid", 40),
    TECHNICIAN("Technician", 50),
    SUPERVISOR("Supervisor", 60),
    ANALYSIS_READ("Analysis Read", 70),
    ADMIN("Admin", 80);

    private final String description;
    private final int value;

    /**
     *
     * @param description
     * @param value - used for comparing specific access
     */
    UserLevelEnum(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description;
    }
}
