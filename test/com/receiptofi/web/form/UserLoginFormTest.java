/**
 *
 */
package com.receiptofi.web.form;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hitender
 * @since Mar 19, 2013 11:47:13 PM
 *
 */
public class UserLoginFormTest {
	private UserLoginForm userLogin;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		userLogin = UserLoginForm.newInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		userLogin = null;
	}

	/**
	 * Test method for {@link com.receiptofi.web.form.UserLoginForm#newInstance()}.
	 */
	@Test
	public final void testNewInstance() {
		UserLoginForm userLogin = UserLoginForm.newInstance();
		assertNotNull(userLogin);
	}

	/**
	 * Test method for {@link com.receiptofi.web.form.UserLoginForm#getEmailId()}.
	 * Test method for {@link com.receiptofi.web.form.UserLoginForm#setEmailId(java.lang.String)}.
	 */
	@Test
	public final void testSetAndGetEmailId() {
		assertNull(userLogin.getEmailId());
		userLogin.setEmailId("test@tholix.com");
		assertNotNull(userLogin.getEmailId());
		userLogin.setEmailId(null);
	}

	/**
	 * Test method for {@link com.receiptofi.web.form.UserLoginForm#getPassword()}.
	 * Test method for {@link com.receiptofi.web.form.UserLoginForm#setPassword(java.lang.String)}.
	 */
	@Test
	public final void testGetPassword() {
		assertNull(userLogin.getPassword());
		userLogin.setPassword("test");
		assertNotNull(userLogin.getPassword());
		userLogin.setPassword(null);
	}
}
