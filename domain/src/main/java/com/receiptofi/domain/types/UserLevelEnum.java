/**
 *
 */
package com.receiptofi.domain.types;

/**
 * @author hitender
 * @since Mar 25, 2013 1:11:21 AM
 */
public enum UserLevelEnum {
    USER("User", 10),
    USER_COMMUNITY("User Community", 20),
    USER_PAID("User Paid", 20),
    EMPLOYER("Employer", 30),
    EMPLOYER_COMMUNITY("Employer Community", 40),
    EMPLOYER_PAID("Employer Paid", 40),
    TECHNICIAN("Technician", 50),
    SUPERVISOR_READ("Supervisor Read", 60),
    SUPERVISOR("Supervisor", 60),
    ADMIN("Admin", 70);

    public final String description;
    public final int value;

    /**
     *
     * @param description
     * @param value - used for comparing specific access
     */
    private UserLevelEnum(String description, int value) {
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
