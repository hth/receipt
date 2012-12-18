/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

/**
 * @author hitender 
 * Dec 15, 2012 8:11:45 PM
 */
public class ReceiptUser implements Serializable {

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
	
	public static ReceiptUser getReceiptUserInstance() {
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
