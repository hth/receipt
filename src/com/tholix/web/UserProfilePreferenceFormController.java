/**
 * 
 */
package com.tholix.web;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	private static final Logger log = Logger.getLogger(UserProfilePreferenceFormController.class);
	
	private static final String nextPage = "/userprofilepreference";

	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserPreferenceManager userPreferenceManager;
	
	@RequestMapping(value = "/i", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userProfile") UserProfileEntity userProfile, @ModelAttribute("userPreference") UserPreferenceEntity userPreference, HttpSession session) {
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		
		userProfile = userProfileManager.getObjectUsingEmail(userSession.getEmailId());
		ModelAndView modelAndView = populateData(userProfile);
//		userPreference = userPreferenceManager.getObjectUsingUserProfile(userProfile);
//		
//		ModelAndView modelAndView = new ModelAndView(nextPage);		
//		modelAndView.addObject("userProfile", userProfile);
//		modelAndView.addObject("userPreference", userPreference);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/their", method = RequestMethod.GET)
	public ModelAndView getUser(@RequestParam("id") String id) {		
		UserProfileEntity userProfile = userProfileManager.findOne(id);
		ModelAndView modelAndView = populateData(userProfile);
		
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView getUser(@ModelAttribute("userProfile") UserProfileEntity userProfile, HttpSession session) {		
		userProfileManager.updateObject(userProfile.getId(), userProfile.getLevel());
		
		userProfile = userProfileManager.findOne(userProfile.getId());
		ModelAndView modelAndView = populateData(userProfile);
		
		return modelAndView;
	}

	/**
	 * @param userProfile
	 * @return
	 */
	private ModelAndView populateData(UserProfileEntity userProfile) {
		UserPreferenceEntity userPreference = userPreferenceManager.getObjectUsingUserProfile(userProfile);
		
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
