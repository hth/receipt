/**
 * 
 */
package com.tholix.web.form;

import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender 
 * @when Mar 26, 2013 3:52:26 PM
 *
 */
public class UserSearchForm {

	private String id;
	private String name = "";
	private String firstName = "";
	private String lastName = "";
	private UserLevelEnum level;
	
	/** To make bean happy */
	private UserSearchForm() {}
	
	private UserSearchForm(String id, String firstName, String lastName, UserLevelEnum level) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.level = level;
		this.name = lastName + ", " + firstName;
	}
	
	public static UserSearchForm newInstance() {
		return new UserSearchForm();
	}
	
	public static UserSearchForm newInstance(String id, String firstName, String lastName, UserLevelEnum level) {
		return new UserSearchForm(id, firstName, lastName, level);
	}
	
	public static UserSearchForm newInstance(UserProfileEntity userProfile) {
		return new UserSearchForm(userProfile.getId(), userProfile.getFirstName(), userProfile.getLastName(), userProfile.getLevel());
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		if(name.length() > 2 && !name.equalsIgnoreCase(", ")) {
			return name;
		} 
		return "";
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public UserLevelEnum getLevel() {
		return level;
	}
	
	public void setLevel(UserLevelEnum level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "UserSearchForm [id=" + id + ", name=" + name + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
	
	
}
