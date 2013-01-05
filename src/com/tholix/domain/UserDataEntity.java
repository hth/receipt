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
@CompoundIndexes({ @CompoundIndex(name = "user_data_idx", def = "{'user': 1, 'clientName': -1}") })
public class UserDataEntity extends BaseEntity {

	private static final long serialVersionUID = 4809841412952941731L;

	@DBRef
	private UserEntity user;

	/** Name of a business or a client */
	@Size(min = 1, max = 128)
	private String clientName;

	//@PersistenceConstructor
	private UserDataEntity(String clientName, UserEntity user) {
		super();
		this.clientName = clientName;
		this.user = user;
	}

	/**
	 * This method is used when the Entity is created for the first time. 
	 * 
	 * @param clientName
	 * @param user
	 * @return
	 */
	public static UserDataEntity newInstance(String clientName, UserEntity user) {
		return new UserDataEntity(clientName, user);
	}

	public UserEntity getUser() {
		return user;
	}

	public void setReceiptUser(UserEntity user) {
		this.user = user;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
