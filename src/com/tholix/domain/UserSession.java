/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @when Jan 4, 2013 4:58:58 PM
 * 
 *       Used in session
 */
public class UserSession implements Serializable {
	private static final long serialVersionUID = 7575677662361932482L;
    private static volatile Logger log = Logger.getLogger(UserSession.class);

	String emailId;
	String userProfileId;
	long pendingCount;
	private UserLevelEnum level;
	
	/** To make bean happy */
	private UserSession() {
		
	}

	private UserSession(String emailId, String userProfileId, UserLevelEnum level) {
		this.emailId = emailId;
		this.userProfileId = userProfileId;
		this.level = level;
	}

	public static UserSession newInstance(String emailId, String userProfileId, UserLevelEnum level) {
		return new UserSession(emailId, userProfileId, level);
	}

	public String getEmailId() {
        if(emailId == null) {
            log.error("Email Id is NUll");
            return "";
        }
		return emailId;
	}

	public String getUserProfileId() {
        if(userProfileId == null) {
            log.error("User profile Id is NULL");
            return "";
        }
		return userProfileId;
	}
	
	public UserLevelEnum getLevel() {
		return this.level;
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
