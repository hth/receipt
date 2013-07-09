/**
 *
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author hitender
 * @since Dec 15, 2012 8:11:45 PM
 *
 * The link below is for field annotation settings
 * {@link http://www.jpalace.org/docs/tutorials/spring/mvc_21.html}
 */
@Document(collection = "USER_AUTHENTICATION")
@CompoundIndexes({ @CompoundIndex(name = "user_authentication_idx", def = "{'PASSWORD': 1}") })
public class UserAuthenticationEntity extends BaseEntity {
	private static final long serialVersionUID = -5207492124434434278L;

	@NotNull
    @Field("PASSWORD")
	private String password;

	@NotNull
    @Field("AUTH")
	private String authenticationKey;

	/**
	 * Required for Bean Instantiation
	 */
    @SuppressWarnings("unused")
	private UserAuthenticationEntity() {}

	/**
	 *
	 * @param password
	 */
	private UserAuthenticationEntity(String password, String authenticationKey) {
		this.password = password;
		this.authenticationKey = authenticationKey;
	}

	/**
	 * This method is used when the Entity is created for the first time.
	 *
	 * @param password
	 * @param authenticationKey - (password + time stamp) to HashCode this needs to go to OAuth
	 * @return
	 */
	public static UserAuthenticationEntity newInstance(String password, String authenticationKey) {
		return new UserAuthenticationEntity(password, authenticationKey);
	}

	public String getPassword() {
		return password;
	}

	//TODO implement OAuth
	public String getAuthenticationKey() {
		return authenticationKey;
	}
}
