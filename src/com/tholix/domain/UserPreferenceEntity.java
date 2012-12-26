/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender 
 * @when Dec 23, 2012 1:48:36 AM
 *
 */
@Document(collection="USER_PREFERENCE")
public class UserPreferenceEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1096957451728520230L;
	
	@DBRef
	@Indexed(unique = true)
	private ReceiptUserEntity receiptUser;
	
	@NotNull
	private AccountTypeEnum accountType = AccountTypeEnum.PERSONAL;
	
	@PersistenceConstructor
	private UserPreferenceEntity(AccountTypeEnum accountType, ReceiptUserEntity receiptUser) {
		super();
		this.accountType = accountType;
		this.receiptUser = receiptUser;
	}
	
	public static UserPreferenceEntity newInstance(AccountTypeEnum accountType, ReceiptUserEntity receiptUser) {
		return new UserPreferenceEntity(accountType, receiptUser);
	}

	public ReceiptUserEntity getReceiptUser() {
		return receiptUser;
	}

	public void setReceiptUser(ReceiptUserEntity receiptUser) {
		this.receiptUser = receiptUser;
	}

	public AccountTypeEnum getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountTypeEnum accountType) {
		this.accountType = accountType;
	}
	
	
}
