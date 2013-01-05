/**
 * 
 */
package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.types.AccountTypeEnum;

/**
 * @author hitender
 * @when Dec 23, 2012 1:48:36 AM
 * 
 */
@Document(collection = "USER_PREFERENCE")
public class UserPreferenceEntity extends BaseEntity {

	private static final long serialVersionUID = 1096957451728520230L;

	@DBRef
	@Indexed(unique = true)
	private UserProfileEntity userProfile;

	@NotNull
	private AccountTypeEnum accountType = AccountTypeEnum.PERSONAL;

	//@PersistenceConstructor
	private UserPreferenceEntity(AccountTypeEnum accountType, UserProfileEntity userProfile) {
		super();
		this.accountType = accountType;
		this.userProfile = userProfile;
	}

	/**
	 * This method is used when the Entity is created for the first time. 
	 * 
	 * @param accountType
	 * @param user
	 * @return
	 */
	public static UserPreferenceEntity newInstance(AccountTypeEnum accountType, UserProfileEntity userProfile) {
		return new UserPreferenceEntity(accountType, userProfile);
	}	

	public UserProfileEntity getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfileEntity userProfile) {
		this.userProfile = userProfile;
	}

	public AccountTypeEnum getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountTypeEnum accountType) {
		this.accountType = accountType;
	}

}
