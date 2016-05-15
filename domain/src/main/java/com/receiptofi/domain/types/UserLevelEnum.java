/**
 *
 */
package com.receiptofi.domain.types;

/**
 * User level defines the roles set for a user in UserAccountEntity.
 * Roles in UserAccountEntity sets authorities in ReceiptUser.
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
    ENTERPRISE("Enterprise", 30),
    ENTERPRISE_COMMUNITY("Enterprise Community", 40),
    ENTERPRISE_PAID("Enterprise Paid", 40),
    BUSINESS_SMALL("Business Small", 50),
    BUSINESS_LARGE("Business Large", 60),
    TECHNICIAN("Technician", 70),
    SUPERVISOR("Supervisor", 80),
    ANALYSIS_READ("Analysis Read", 90),
    ADMIN("Admin", 100);

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
