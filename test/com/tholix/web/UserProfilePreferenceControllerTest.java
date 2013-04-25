package com.tholix.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.repository.UserPreferenceManager;
import com.tholix.repository.UserProfileManager;

/**
 * @author hitender
 * @when Mar 23, 2013 11:04:26 PM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class UserProfilePreferenceControllerTest {
	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserPreferenceManager userPreferenceManager;

	private UserProfilePreferenceController controller;

    @Mock private BindingResult result;

	@Before
	public void setUp() throws Exception {
		controller = new UserProfilePreferenceController();

		MockitoAnnotations.initMocks(this);
	    Mockito.when(result.hasErrors()).thenReturn(false);
	}

	@After
	public void tearDown() throws Exception {
		userProfileManager = null;
		userPreferenceManager = null;

		controller = null;

		result = null;
	}

	@Test
	public void testLoadForm() {
		controller.setUserPreferenceManager(userPreferenceManager);
		controller.setUserProfileManager(userProfileManager);

		UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail("test@test.com");
		UserPreferenceEntity userPreference = userPreferenceManager.getObjectUsingUserProfile(userProfile);
        UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId(), UserLevelEnum.USER);

		ModelAndView modelAndView = controller.loadForm(userProfile, userPreference, userSession);
		assertNotNull(modelAndView);

		assertEquals("/userprofilepreference", modelAndView.getViewName());

		UserProfileEntity userProfileActual = (UserProfileEntity) modelAndView.getModelMap().get("userProfile");
		assertEquals(userProfile.getId(), userProfileActual.getId());

		@SuppressWarnings("unchecked")
		UserPreferenceEntity userPreferenceActual = (UserPreferenceEntity) modelAndView.getModelMap().get("userPreference");
		assertEquals(userPreference.getId(), userPreferenceActual.getId());
	}

}
