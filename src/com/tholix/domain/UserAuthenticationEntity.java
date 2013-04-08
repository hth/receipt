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
 * {@link http://www.jpalace.org/docs/tutorials/spring/mvc_21.html}
 */
@Document(collection = "USER_AUTHENTICATION")
public class UserAuthenticationEntity extends BaseEntity {
	private static final long serialVersionUID = -5207492124434434278L;

	@NotNull
	private String password;
	
	@NotNull
	private String auth;

	/**
	 * Required for Bean Instantiation
	 */
	private UserAuthenticationEntity() { }

	/**
	 * 
	 * @param password
	 */
	// @PersistenceConstructor	
	private UserAuthenticationEntity(String password, String auth) {
		this.password = password;
		this.auth = auth;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 * 
	 * @param password
	 * @param auth - (password + time stamp) to HashCode this needs to go to OAuth
	 * @return
	 */
	public static UserAuthenticationEntity newInstance(String password, String auth) {
		return new UserAuthenticationEntity(password, auth);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	//TODO implement OAuth 
	public String getAuth() {
		return auth;
	}
}
