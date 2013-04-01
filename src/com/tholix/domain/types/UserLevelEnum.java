/**
 * 
 */
package com.tholix.domain.types;

/**
 * @author hitender 
 * @when Mar 25, 2013 1:11:21 AM
 *
 */
public enum UserLevelEnum {
	
	USER("USER", "User", 1, 4), 
	EMPLOYER("EMPLOYER", "Employer", 2, 5),
	USER_PAID("USER_PAID", "User Paid", 3 , 6),
	EMPLOYER_PAID("EMPLOYER_PAID", "Employer Paid", 4, 7),
	WORKER("WORKER", "Worker", 5, 8),
	SUPERVISOR("SUPERVISOR", "Supervisor", 6, 9),
	ADMIN("ADMIN", "Admin", 7, 10),
	;
	
	private final String description;
	private final String name;
	private final int value;
	private final int messagePriorityJMS;

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
