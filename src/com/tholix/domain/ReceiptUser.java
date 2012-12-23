/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.utils.SHAHashing;

/**
 * @author hitender 
 * @when Dec 15, 2012 8:11:45 PM
 */
@Document(collection="USER")
public class ReceiptUser implements Serializable {
	private static final long serialVersionUID = -5207492124434434278L;

	@Id
	private ObjectId id;
	
	@Indexed(unique = true)
	private String emailId;
	
	@Transient
	private String password;		
	private String passwordHash;

	/**
	 * Required for Bean Instantiation
	 */
	private ReceiptUser() {
	}

	private ReceiptUser(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * Does not event gets instantiated 
	 * 
	 * @param emailId
	 * @param password
	 */
	private ReceiptUser(String emailId, String password) {
		this.emailId = emailId;
		this.passwordHash = SHAHashing.hashCode(password);
	}

	public static ReceiptUser findReceiptUser(String emailId) {
		return new ReceiptUser(emailId);
	}
	
	private static ReceiptUser newInstance(String emailId, String password) {
		return new ReceiptUser(emailId, password);
	}	

	/** Exposed for unit test mostly */
	public ObjectId getId() {
		return id;
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
		if (!(obj instanceof ReceiptUser))
			return false;
		ReceiptUser other = (ReceiptUser) obj;
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
