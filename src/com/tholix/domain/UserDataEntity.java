/**
 * 
 */
package com.tholix.domain;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Size;

/**
 * @author hitender 
 * @when Dec 23, 2012 2:48:01 PM
 *
 */
@Document(collection="USER_DATA")
@CompoundIndexes({
  @CompoundIndex(name = "user_data_idx", def = "{'receiptUser': 1, 'clientName': -1}")
})
public class UserDataEntity extends BaseEntity {
	
	private static final long serialVersionUID = 4809841412952941731L;
	
	@DBRef
	private ReceiptUserEntity receiptUser;
	
	/** Name of a business or a client */
	@Size(min=1, max=128)
	private String clientName;
	
	@PersistenceConstructor
	private UserDataEntity(String clientName, ReceiptUserEntity receiptUser) {
		super();
		this.clientName = clientName;
		this.receiptUser = receiptUser;
	}
	
	public static UserDataEntity newInstance(String clientName, ReceiptUserEntity receiptUser) {
		return new UserDataEntity(clientName, receiptUser);
	}

	public ReceiptUserEntity getReceiptUser() {
		return receiptUser;
	}

	public void setReceiptUser(ReceiptUserEntity receiptUser) {
		this.receiptUser = receiptUser;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	

}
