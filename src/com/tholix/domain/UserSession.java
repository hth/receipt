/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

/**
 * @author hitender
 * @when Jan 4, 2013 4:58:58 PM
 * 
 * Used in session
 */
public class UserSession implements Serializable {
	private static final long serialVersionUID = 7575677662361932482L;

	String emailId;
	String profileId;

	private UserSession(String emailId, String profileId) {
		this.emailId = emailId;
		this.profileId = profileId;
	}

	public static UserSession newInstance(String emailId, String profileId) {
		return new UserSession(emailId, profileId);
	}

	public String getEmailId() {
		return emailId;
	}

	public String getProfileId() {
		return profileId;
	}
}
