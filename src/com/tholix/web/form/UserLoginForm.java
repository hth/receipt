/**
 *
 */
package com.tholix.web.form;

/**
 * @author hitender
 * @since Jan 4, 2013 4:41:01 PM
 *
 */
public class UserLoginForm {
	private String emailId;
	private String password;

	public static UserLoginForm newInstance() {
		return new UserLoginForm();
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
