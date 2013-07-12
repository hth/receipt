/**
 *
 */
package com.tholix.domain.types;

/**
 * @author hitender
 * @since Mar 25, 2013 1:11:21 AM
 */
public enum UserLevelEnum {
	USER("USER",                    "User",           1, UserLevelEnum.DEFAULT_JMS_LEVEL),
	EMPLOYER("EMPLOYER",            "Employer",       2, UserLevelEnum.DEFAULT_JMS_LEVEL + 1),
	USER_PAID("USER_PAID",          "User Paid",      3, UserLevelEnum.DEFAULT_JMS_LEVEL + 2),
	EMPLOYER_PAID("EMPLOYER_PAID",  "Employer Paid",  4, UserLevelEnum.DEFAULT_JMS_LEVEL + 3),
	TECHNICIAN("TECHNICIAN",        "Technician",     5, UserLevelEnum.DEFAULT_JMS_LEVEL + 4),
	SUPERVISOR("SUPERVISOR",        "Supervisor",     6, UserLevelEnum.DEFAULT_JMS_LEVEL + 5),
	ADMIN("ADMIN",                  "Admin",          7, UserLevelEnum.DEFAULT_JMS_LEVEL + 6),
	;

    //TODO to use JMS message setting in future. Currently message is picked based on level of the user.
    private static final int DEFAULT_JMS_LEVEL = 4;

    public final String description;
    public final String name;
    public final int value;
    public final int messagePriorityJMS;

    private UserLevelEnum(String name, String description, int value, int messagePriorityJMS) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.messagePriorityJMS = messagePriorityJMS;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * This gets you level value. More like the order of precedence.
     *
     * @return
     */
    public int getValue() {
        return value;
    }

    public int getMessagePriorityJMS() {
        return messagePriorityJMS;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
