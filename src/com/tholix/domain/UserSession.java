/**
 *
 */
package com.tholix.domain;

import java.io.Serializable;

import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @since Jan 4, 2013 4:58:58 PM
 *
 *       Used in session
 */
public final class UserSession implements Serializable {
	private static final long serialVersionUID = 7575677662361932482L;

	String emailId;
	String userProfileId;
	private UserLevelEnum level;

	/** To make bean happy */
    @SuppressWarnings("unused")
	private UserSession() {}

	private UserSession(String emailId, String userProfileId, UserLevelEnum level) {
		this.emailId = emailId;
		this.userProfileId = userProfileId;
		this.level = level;
	}

	public static UserSession newInstance(String emailId, String userProfileId, UserLevelEnum level) {
		return new UserSession(emailId, userProfileId, level);
	}

	public String getEmailId() {
		return emailId;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public UserLevelEnum getLevel() {
		return this.level;
	}
}
