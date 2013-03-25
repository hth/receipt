/**
 * 
 */
package com.tholix.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;

/**
 * @author hitender 
 * @when Jan 14, 2013 11:06:41 PM
 *
 */
@Controller
@RequestMapping(value = "/userprofilepreference")
public class UserProfilePreferenceFormController {
	private static final String nextPage = "/userprofilepreference";

	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserPreferenceManager userPreferenceManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userProfile") UserProfileEntity userProfile, @ModelAttribute("userPreference") UserPreferenceEntity userPreference, HttpSession session) {
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		
		userProfile = userProfileManager.getObjectUsingEmail(userSession.getEmailId());
		userPreference = userPreferenceManager.getObjectUsingUserProfile(userProfile);
		
		ModelAndView modelAndView = new ModelAndView(nextPage);		
		modelAndView.addObject("userProfile", userProfile);
		modelAndView.addObject("userPreference", userPreference);
		
		return modelAndView;
	}

	public void setUserProfileManager(UserProfileManager userProfileManager) {
		this.userProfileManager = userProfileManager;
	}

	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager) {
		this.userPreferenceManager = userPreferenceManager;
	}	
}
