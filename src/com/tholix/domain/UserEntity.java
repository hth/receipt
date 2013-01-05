/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender
 * @when Dec 15, 2012 8:11:45 PM
 * 
 * The link below is for field annotation settings
 * @see http://www.jpalace.org/docs/tutorials/spring/mvc_21.html
 */
@Document(collection = "USER_AUTHENTICATION")
public class UserEntity extends BaseEntity {
	private static final long serialVersionUID = -5207492124434434278L;	

	@NotNull
	private String password;

	/**
	 * Required for Bean Instantiation
	 */
	private UserEntity() {
	}

	/**
	 * 
	 * @param password
	 */
	//@PersistenceConstructor
	private UserEntity(String password) {
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
	public static UserEntity newInstance(String password) {
		return new UserEntity(password);
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
}
