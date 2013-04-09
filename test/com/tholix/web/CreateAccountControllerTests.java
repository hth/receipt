/**
 * 
 */
package com.tholix.web;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UserRegistrationValidator;
import com.tholix.web.form.UserRegistrationForm;

/**
 * @author hitender
 * @when Dec 27, 2012 5:48:20 PM
 * @link http://hamletdarcy.blogspot.com/2008/12/autowired-junit-tests-with-spring-25.html
 * @link http://giannisapi.wordpress.com/2011/09/30/spring-3-testing-with-junit-4-using-contextconfiguration-and-abstracttransactionaljunit4springcontexttests/
 * @link http://www.springbyexample.org/examples/intro-to-ioc-unit-test-beans-from-application-context.html
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class CreateAccountControllerTests {
	
	@Autowired
	@Qualifier("userAuthenticationManager")
	private UserAuthenticationManager userAuthenticationManager;
	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserPreferenceManager userPreferenceManager;
	@Autowired private UserRegistrationValidator userRegistrationValidator;
	
	private Model model;
	private CreateAccountController controller;
	private UserRegistrationForm userRegistrationForm;
	
	@Mock BindingResult result;
	@Mock RedirectAttributes redirectAttrs;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//This will inject any mocked objects in the test class, so in this case it will inject mockedObject in testObject. This was mentioned above but here is the code.
		MockitoAnnotations.initMocks(this);
		
		/**
		 * {@link reference - http://stackoverflow.com/questions/8299607/junit-testing-for-annotated-controller}
		 */
		// While the default boolean return value for a mock is 'false',
	    // it's good to be explicit anyway:  
	    Mockito.when(result.hasErrors()).thenReturn(false);		
		
		model = new ExtendedModelMap();
		controller = new CreateAccountController();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		userAuthenticationManager = null;
		userProfileManager = null;
		userPreferenceManager = null;
		userRegistrationValidator = null;		
		controller = null;
		model = null;
		userRegistrationForm = null;
		result = null;
		redirectAttrs = null;
	}
	
	@Test
    public void shouldAutowireDependencies() {
		assertNotNull(userAuthenticationManager);
		assertNotNull(userProfileManager);
        assertNotNull(userPreferenceManager);
        assertNotNull(userRegistrationValidator);
    }

	/**
	 * Test method for {@link com.tholix.web.CreateAccountController#getUserRegistrationForm()}.
	 */
	@Test
	public void testGetUserRegistrationForm() {
		assertNotNull(controller.getUserRegistrationForm());
	}

	/**
	 * Test method for {@link com.tholix.web.CreateAccountController#loadForm(org.springframework.ui.Model)}.
	 */
	@Test
	public void testLoadForm() {
		assertEquals("new", controller.loadForm(model));
	}

	/**
	 * Test method for {@link com.tholix.web.CreateAccountController#post(com.tholix.web.form.UserRegistrationForm, org.springframework.validation.BindingResult, org.springframework.web.servlet.mvc.support.RedirectAttributes)}.
	 */
	@Test
	public void testPost() {
		controller.setUserAuthenticationManager(userAuthenticationManager);
        controller.setUserProfileManager(userProfileManager);
        controller.setUserPreferenceManager(userPreferenceManager);
        controller.setUserRegistrationValidator(userRegistrationValidator);        
        
        userRegistrationForm = UserRegistrationForm.newInstance();
        userRegistrationForm.setFirstName("First");
        userRegistrationForm.setLastName("Last");
        userRegistrationForm.setEmailId("me@me");
        userRegistrationForm.setPassword("see");
        result = new BeanPropertyBindingResult(userRegistrationForm, "userRegistrationForm");
        assertEquals("new", controller.post(userRegistrationForm, result, redirectAttrs));
        
        userRegistrationForm.setFirstName("First Dummy");
        userRegistrationForm.setLastName("Last Dummy");
        userRegistrationForm.setEmailId("dummy@tholix.com");
        userRegistrationForm.setPassword("dummy");
        result = new BeanPropertyBindingResult(userRegistrationForm, "userRegistrationForm");
        assertEquals("redirect:/landing.htm", controller.post(userRegistrationForm, result, redirectAttrs));        
        
        /** Clean up - Remove the user dummy@tholix.com */
        UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("dummy@tholix.com");
        UserAuthenticationEntity user = userProfile.getUserAuthentication();
        UserPreferenceEntity preference = userPreferenceManager.getObjectUsingUserProfile(userProfile);

        userAuthenticationManager.delete(user);
        userProfileManager.delete(userProfile);
        userPreferenceManager.delete(preference);
        
        userProfile = userProfileManager.getObjectUsingEmail("dummy@tholix.com");   
        assertNull(userProfile);
        
        user = userAuthenticationManager.findOne(user.getId());
        assertNull(user);
        
        preference = userPreferenceManager.findOne(preference.getId());
        assertNull(preference);
	}
}