package com.tholix.web;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;

/**
 * @author hitender 
 * @when Mar 23, 2013 11:04:26 PM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class UserProfilePreferenceFormControllerTest {
	@Autowired UserProfileManager userProfileManager;
	@Autowired UserPreferenceManager userPreferenceManager;
	
	private UserProfilePreferenceFormController controller;
    private MockHttpSession session;
    private UserSession userSession;
    
    @Mock private BindingResult result;

	@Before
	public void setUp() throws Exception {
		controller = new UserProfilePreferenceFormController();
		session = new MockHttpSession();
		
		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);	
	}

	@After
	public void tearDown() throws Exception {
		userProfileManager = null;
		userPreferenceManager = null;
		
		controller = null;
		session = null;
		userSession = null;
		
		result = null;
	}

	@Test
	public void testLoadForm() {
		controller.setUserPreferenceManager(userPreferenceManager);
		controller.setUserProfileManager(userProfileManager);
		
		UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
		UserPreferenceEntity userPreference = userPreferenceManager.getObjectUsingUserProfile(userProfile);
		userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId());
		session.setAttribute("userSession", userSession);
		
		ModelAndView modelAndView = controller.loadForm(userProfile, userPreference, session);
		assertNotNull(modelAndView);
		
		assertEquals("/userprofilepreference", modelAndView.getViewName());
		
		UserProfileEntity userProfileActual = (UserProfileEntity) modelAndView.getModelMap().get("userProfile");
		assertEquals(userProfile.getId(), userProfileActual.getId());
		
		@SuppressWarnings("unchecked")
		UserPreferenceEntity userPreferenceActual = (UserPreferenceEntity) modelAndView.getModelMap().get("userPreference");
		assertEquals(userPreference.getId(), userPreferenceActual.getId());
	}

}
