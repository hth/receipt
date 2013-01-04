/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import com.tholix.utils.SHAHashing;

/**
 * @author hitender
 * @when Dec 15, 2012 8:11:45 PM
 */
@Document(collection = "USER")
public class UserEntity extends BaseEntity {
	private static final long serialVersionUID = -5207492124434434278L;

	@Indexed(unique = true)
	private String emailId;

	// TODO follow http://www.jpalace.org/docs/tutorials/spring/mvc_21.html
	@NotNull
	private String password;

	/**
	 * Required for Bean Instantiation
	 */
	private UserEntity() {
	}

	private UserEntity(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * 
	 * @param emailId
	 * @param password
	 */
	//@PersistenceConstructor
	private UserEntity(String emailId, String password) {
		this.emailId = emailId;
		this.password =  password; 
	}

	public static UserEntity findUser(String emailId) {
		return new UserEntity(emailId);
	}

	/**
	 * This method is used when the Entity is created for the first time. 
	 * 
	 * @param emailId
	 * @param passwordHash
	 * @return
	 */
	public static UserEntity newInstance(String emailId, String password) {
		return new UserEntity(emailId, password);
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailId == null) ? 0 : emailId.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserEntity))
			return false;
		UserEntity other = (UserEntity) obj;
		if (emailId == null) {
			if (other.emailId != null)
				return false;
		} else if (!emailId.equals(other.emailId))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReceiptUser [emailId=" + emailId + "]";
	}
}
