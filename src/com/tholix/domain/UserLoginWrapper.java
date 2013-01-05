/**
 * 
 */
package com.tholix.domain;

/**
 * @author hitender
 * @when Jan 4, 2013 4:41:01 PM
 * 
 */
public class UserLoginWrapper {
	private String emailId;
	private String password;
	
	public static UserLoginWrapper newInstance() {
		return new UserLoginWrapper();
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
