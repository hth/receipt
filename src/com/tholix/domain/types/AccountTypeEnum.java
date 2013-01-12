/**
 * 
 */
package com.tholix.domain.types;

/**
 * @author hitender
 * @when Dec 23, 2012 11:06:13 AM
 * 
 */
public enum AccountTypeEnum {

	PERSONAL("PERSONAL", "Personal"), 
	PERSONAL_BUSINESS("PERSONAL_BUSINESS", "Personal & Business"), 
	BUSINESS_CLIENT("BUSINESS_CLIENT", "Business with multiple clients")
	;

	private final String description;
	private final String name;

	private AccountTypeEnum(String name, String description) {
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
