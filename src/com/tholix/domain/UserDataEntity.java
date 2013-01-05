/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender
 * @when Dec 23, 2012 2:48:01 PM
 * 
 */
@Document(collection = "USER_DATA")
@CompoundIndexes({ @CompoundIndex(name = "user_data_idx", def = "{'userAuthentication': 1, 'clientName': -1}") })
public class UserDataEntity extends BaseEntity {

	private static final long serialVersionUID = 4809841412952941731L;

	@DBRef
	private UserAuthenticationEntity userAuthentication;

	/** Name of a business or a client */
	@Size(min = 1, max = 128)
	private String clientName;

	//@PersistenceConstructor
	private UserDataEntity(String clientName, UserAuthenticationEntity userAuthentication) {
		super();
		this.clientName = clientName;
		this.userAuthentication = userAuthentication;
	}

	/**
	 * This method is used when the Entity is created for the first time. 
	 * 
	 * @param clientName
	 * @param userAuthentication
	 * @return
	 */
	public static UserDataEntity newInstance(String clientName, UserAuthenticationEntity userAuthentication) {
		return new UserDataEntity(clientName, userAuthentication);
	}	

	public UserAuthenticationEntity getUserAuthentication() {
		return userAuthentication;
	}

	public void setUserAuthentication(UserAuthenticationEntity userAuthentication) {
		this.userAuthentication = userAuthentication;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
