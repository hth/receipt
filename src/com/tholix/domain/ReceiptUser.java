/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hitender 
 * Dec 15, 2012 8:11:45 PM
 */
public class ReceiptUser implements Serializable {
	private static final long serialVersionUID = -5207492124434434278L;
	
	private String emailId;
	private String password;
	
	private ReceiptUser() {
		
	}

	private ReceiptUser(String emailId) {
		this.emailId = emailId;
	}

	private ReceiptUser(String emailId, String password) {
		this.emailId = emailId;
		this.password = password;
	}

	public static ReceiptUser getReceiptUserForFinding(String emailId) {
		return new ReceiptUser(emailId);
	}

	public static ReceiptUser getReceiptUserForSignup(String emailId, String password) {
		return new ReceiptUser(emailId, password);
	}
	
	private static ReceiptUser getReceiptUserInstance() {
		return new ReceiptUser();
	}

	public String getEmailId() {
		return emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
}
