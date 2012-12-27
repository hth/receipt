/**
 * 
 */
package com.tholix.domain;

import org.joda.time.DateTime;

/**
 * @author hitender
 * @when Dec 25, 2012 12:01:53 PM
 * 
 */
public class NewUserWrapper {

	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private AccountTypeEnum accountType;

	public static NewUserWrapper newInstance() {
		return new NewUserWrapper();
	}

	/**
	 * Gets a new instance of Receipt User
	 * 
	 * @return
	 */
	public UserEntity newReceiptUserEntity() {
		return UserEntity.newInstance(emailId, password);
	}

	public UserProfileEntity newUserProfileEntity(UserEntity receiptUser) {
		return UserProfileEntity.newInstance(firstName, lastName, DateTime.now().toDate(), receiptUser);
	}

	public UserPreferenceEntity newUserPreferenceEntity(UserEntity receiptUser) {
		return UserPreferenceEntity.newInstance(accountType, receiptUser);
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
