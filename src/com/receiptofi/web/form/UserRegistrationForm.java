/**
 *
 */
package com.receiptofi.web.form;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.AccountTypeEnum;
import com.receiptofi.utils.RandomString;
import com.receiptofi.utils.SHAHashing;

/**
 * @author hitender
 * @since Dec 25, 2012 12:01:53 PM
 *
 */
public final class UserRegistrationForm {

	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private AccountTypeEnum accountType;

    private UserRegistrationForm() {}

	public static UserRegistrationForm newInstance() {
		return new UserRegistrationForm();
	}

	/**
	 * Gets a new instance of Receipt User
	 *
	 * @return
	 */
	public UserAuthenticationEntity newUserAuthenticationEntity() {
		return UserAuthenticationEntity.newInstance(SHAHashing.hashCodeSHA512(password), SHAHashing.hashCodeSHA1(RandomString.newInstance().nextString()));
	}

	public UserProfileEntity newUserProfileEntity(UserAuthenticationEntity userAuthentication) {
		return UserProfileEntity.newInstance(emailId, firstName, lastName, DateTime.now().toDate(), userAuthentication);
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

    /**
     * During registration make sure all the email ids are lowered case.
     *
     * @return
     */
	public String getEmailId() {
		return StringUtils.lowerCase(emailId);
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

	@Override
	public String toString() {
		return "UserRegistrationForm [firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId + ", password=" + password + ", accountType=" + accountType + "]";
	}
}
