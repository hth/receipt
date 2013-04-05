/**
 * 
 */
package com.tholix.web.form;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.AccountTypeEnum;

/**
 * @author hitender 
 * @when Mar 20, 2013 12:03:27 AM
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRegistrationFormTest {
	private UserRegistrationForm userRegistration;
	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private AccountTypeEnum accountType;
	private UserAuthenticationEntity userAuthentication; 
	
	//@Mock
	private UserProfileEntity userProfile;
	private UserPreferenceEntity userPreference;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		userRegistration = UserRegistrationForm.newInstance();	
		firstName = "FirstName";
		lastName = "LastName";
		emailId = "test@tholix.com";
		password = "test";
		accountType = AccountTypeEnum.PERSONAL;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		firstName = null;
		lastName = null;
		emailId = null;
		password = null;
		userRegistration = null;
		userAuthentication = null;
		userProfile = null;
		userPreference = null;
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#newInstance()}.
	 */
	@Test
	public void testNewInstance() {
		assertNotNull(userRegistration);
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#newUserAuthenticationEntity()}.
	 */
	@Test
	public void testNewUserAuthenticationEntity() {
		userRegistration.setPassword(password);
		userAuthentication = userRegistration.newUserAuthenticationEntity();
		assertNotNull(userAuthentication);
		String pp = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff";
		assertEquals(pp, userAuthentication.getPassword());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#newUserProfileEntity(com.tholix.domain.UserAuthenticationEntity)}.
	 */
	@Test
	public void testNewUserProfileEntity() {
		assertNull(userProfile);
		userRegistration.setFirstName(firstName);
		userRegistration.setLastName(lastName);
		userRegistration.setEmailId(emailId);
		userRegistration.setPassword(password);
		userProfile = userRegistration.newUserProfileEntity(userAuthentication);
		assertNotNull(userProfile);
		assertEquals(emailId, userProfile.getEmailId());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#newUserPreferenceEntity(com.tholix.domain.UserProfileEntity)}.
	 */
	@Test
	public void testNewUserPreferenceEntity() {
		assertNull(userPreference);
		userRegistration.setAccountType(accountType);
		userPreference = userRegistration.newUserPreferenceEntity(userProfile);
		assertNotNull(userPreference);
		assertEquals(AccountTypeEnum.PERSONAL, userPreference.getAccountType());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#getFirstName()}.
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#setFirstName(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetFirstName() {
		assertNull(userRegistration.getFirstName());
		userRegistration.setFirstName(firstName);
		assertEquals(firstName, userRegistration.getFirstName());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#getLastName()}.
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#setLastName(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetLastName() {
		assertNull(userRegistration.getLastName());
		userRegistration.setLastName(lastName);
		assertEquals(lastName, userRegistration.getLastName());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#getEmailId()}.
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#setEmailId(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetEmailId() {
		assertNull(userRegistration.getEmailId());
		userRegistration.setEmailId(emailId);
		assertEquals(emailId, userRegistration.getEmailId());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#getPassword()}.
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#setPassword(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetPassword() {
		assertNull(userRegistration.getPassword());
		userRegistration.setPassword(password);
		assertEquals(password, userRegistration.getPassword());
	}

	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#getAccountType()}.
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#setAccountType(com.tholix.domain.types.AccountTypeEnum)}.
	 */
	@Test
	public void testGetAndSetAccountType() {
		assertNull(userRegistration.getAccountType());
		userRegistration.setAccountType(AccountTypeEnum.PERSONAL);
		assertEquals(AccountTypeEnum.PERSONAL, userRegistration.getAccountType());
	}
	
	/**
	 * Test method for {@link com.tholix.web.form.UserRegistrationForm#toString()}.
	 */
	@Test
	public void testToString() {
		userRegistration.setFirstName(firstName);
		userRegistration.setLastName(lastName);
		userRegistration.setEmailId(emailId);
		userRegistration.setPassword(password);
		userRegistration.setAccountType(AccountTypeEnum.PERSONAL);
		String testString = "UserRegistrationForm [firstName=FirstName, lastName=LastName, emailId=test@tholix.com, password=test, accountType=Personal]";
		assertEquals(testString, userRegistration.toString());
	}

}
