/**
 * 
 */
package com.tholix.domain;

import org.joda.time.DateTime;

import com.tholix.domain.types.AccountTypeEnum;
import com.tholix.utils.SHAHashing;

/**
 * @author hitender
 * @when Dec 25, 2012 12:01:53 PM
 * 
 */
public class UserRegistrationWrapper {

	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private AccountTypeEnum accountType;

	public static UserRegistrationWrapper newInstance() {
		return new UserRegistrationWrapper();
	}

	/**
	 * Gets a new instance of Receipt User
	 * 
	 * @return
	 */
	public UserEntity newUserEntity() {
		return UserEntity.newInstance(SHAHashing.hashCode(password));
	}

	public UserProfileEntity newUserProfileEntity(UserEntity user) {
		return UserProfileEntity.newInstance(emailId, firstName, lastName, DateTime.now().toDate(), user);
	}

	public UserPreferenceEntity newUserPreferenceEntity(UserProfileEntity userProfile) {
		return UserPreferenceEntity.newInstance(accountType, userProfile);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public AccountTypeEnum getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountTypeEnum accountType) {
		this.accountType = accountType;
	}
}
