/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender Dec 15, 2012 8:11:45 PM
 */
@Document
public class ReceiptUser implements Serializable {
	private static final long serialVersionUID = -5207492124434434278L;

	@Id
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

	public static ReceiptUser findReceiptUser(String emailId) {
		return new ReceiptUser(emailId);
	}

	public static ReceiptUser signupReceiptUser(String emailId, String password) {
		return new ReceiptUser(emailId, password);
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

	@Override
	public String toString() {
		return "ReceiptUser [emailId=" + emailId + ", password=" + password + "]";
	}	
}
