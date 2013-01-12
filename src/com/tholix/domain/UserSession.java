/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

/**
 * @author hitender
 * @when Jan 4, 2013 4:58:58 PM
 * 
 *       Used in session
 */
public class UserSession implements Serializable {
	private static final long serialVersionUID = 7575677662361932482L;

	String emailId;
	String userProfileId;
	long pendingCount;
	
	/** To make bean happy */
	public UserSession() {
		
	}

	private UserSession(String emailId, String userProfileId) {
		this.emailId = emailId;
		this.userProfileId = userProfileId;
	}

	public static UserSession newInstance(String emailId, String userProfileId) {
		return new UserSession(emailId, userProfileId);
	}

	public String getEmailId() {
		return emailId;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public long getPendingCount() {
		return pendingCount;
	}

	public void setPendingCount(long pendingCount) {
		this.pendingCount = pendingCount;
	}
	
	public boolean isEmpty() {
		if(emailId == null || userProfileId == null) {
			return true;
		}
		return false;
	}
}
