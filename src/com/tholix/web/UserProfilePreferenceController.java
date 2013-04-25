/**
 *
 */
package com.tholix.web;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * @author hitender
 * @when Jan 14, 2013 11:06:41 PM
 *
 */
@Controller
@RequestMapping(value = "/userprofilepreference")
@SessionAttributes({"userSession"})
public class UserProfilePreferenceController {
	private static final Logger log = Logger.getLogger(UserProfilePreferenceController.class);

	private static final String nextPage = "/userprofilepreference";

    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

	@RequestMapping(value = "/i", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userProfile") UserProfileEntity userProfile, @ModelAttribute("userPreference") UserPreferenceEntity userPreference, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();

		userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());
        ModelAndView modelAndView = populateModel(userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

	@RequestMapping(value = "/their", method = RequestMethod.GET)
	public ModelAndView getUser(@RequestParam("id") String id) {
        DateTime time = DateUtil.now();

        UserProfileEntity userProfile = userProfilePreferenceService.findById(id);
        ModelAndView modelAndView = populateModel(userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

	@RequestMapping(value="/update", method = RequestMethod.POST)
	public ModelAndView updateUser(@ModelAttribute("userProfile") UserProfileEntity userProfile) {
        DateTime time = DateUtil.now();

        userProfilePreferenceService.updateProfile(userProfile);
		ModelAndView modelAndView = populateModel(userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

	/**
	 * @param userProfile
	 * @return
	 */
	private ModelAndView populateModel(UserProfileEntity userProfile) {
        DateTime time = DateUtil.now();

        UserPreferenceEntity userPreference = userProfilePreferenceService.loadFromProfile(userProfile);
		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("userProfile", userProfile);
		modelAndView.addObject("userPreference", userPreference);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}
}
