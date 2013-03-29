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
	
	USER("USER", "User", 1), 
	USER_PAID("USER_PAID", "User Paid", 2),
	EMPLOYER("EMPLOYER", "Employer", 3),
	EMPLOYER_PAID("EMPLOYER_PAID", "Employer Paid", 4),
	WORKER("WORKER", "Worker", 5),
	SUPERVISOR("SUPERVISOR", "Supervisor", 6),
	ADMIN("ADMIN", "Admin", 7),
	;
	
	private final String description;
	private final String name;
	private final int value;

	private UserLevelEnum(String name, String description, int value) {
		this.name = name;
		this.description = description;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getDescription();
	}
}
