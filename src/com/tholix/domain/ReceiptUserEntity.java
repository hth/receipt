/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.utils.SHAHashing;

/**
 * @author hitender 
 * @when Dec 15, 2012 8:11:45 PM
 */
@Document(collection="USER")
public class ReceiptUserEntity extends BaseEntity {
	private static final long serialVersionUID = -5207492124434434278L;
	
	@Indexed(unique = true)
	private String emailId;
	
	@Transient
	private String password;
	
	//TODO follow http://www.jpalace.org/docs/tutorials/spring/mvc_21.html 
	@NotNull
	private String passwordHash;

	/**
	 * Required for Bean Instantiation
	 */
	private ReceiptUserEntity() {
	}

	private ReceiptUserEntity(String emailId) {
		this.emailId = emailId;
	}
	
	private ReceiptUserEntity(String emailId, String password) {
		this.emailId = emailId;
		this.passwordHash = SHAHashing.hashCode(password);
	}

	public static ReceiptUserEntity findReceiptUser(String emailId) {
		return new ReceiptUserEntity(emailId);
	}
	
	public static ReceiptUserEntity newInstance(String emailId, String password) {
		return new ReceiptUserEntity(emailId, password);
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
		this.passwordHash = SHAHashing.hashCode(password);
	}	

	public String getPasswordHash() {
		return passwordHash;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailId == null) ? 0 : emailId.hashCode());
		result = prime * result + ((passwordHash == null) ? 0 : passwordHash.hashCode());
		return result;
	} 
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ReceiptUserEntity))
			return false;
		ReceiptUserEntity other = (ReceiptUserEntity) obj;
		if (emailId == null) {
			if (other.emailId != null)
				return false;
		} else if (!emailId.equals(other.emailId))
			return false;
		if (passwordHash == null) {
			if (other.passwordHash != null)
				return false;
		} else if (!passwordHash.equals(other.passwordHash))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReceiptUser [emailId=" + emailId + "]";
	}	
}
