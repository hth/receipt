/**
 * 
 */
package com.tholix.domain.types;

/**
 * @author hitender 
 * @when Dec 27, 2012 1:22:39 PM
 *
 */
public enum TaxEnum {
	
	TAXED("TAXED", "Taxed"), 
	NOT_TAXED("NOT_TAXED", "Not Taxed")
	;
	
	private final String description;
	private final String name;

	private TaxEnum(String name, String description) {
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
