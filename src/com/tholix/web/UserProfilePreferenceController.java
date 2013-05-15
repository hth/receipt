/**
 *
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.validator.ExpenseTypeValidator;

/**
 * @author hitender
 * @since Jan 14, 2013 11:06:41 PM
 *
 */
@Controller
@RequestMapping(value = "/userprofilepreference")
@SessionAttributes({"userSession"})
public class UserProfilePreferenceController {
	private static final Logger log = Logger.getLogger(UserProfilePreferenceController.class);

	private static final String nextPage = "/userprofilepreference";

    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private ExpenseTypeValidator expenseTypeValidator;

	@RequestMapping(value = "/i", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();

        UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());
        ModelAndView modelAndView = populateModel(nextPage, userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

	@RequestMapping(value = "/their", method = RequestMethod.GET)
	public ModelAndView getUser(@RequestParam("id") String id) {
        DateTime time = DateUtil.now();

        UserProfileEntity userProfile = userProfilePreferenceService.findById(id);
        ModelAndView modelAndView = populateModel(nextPage, userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

	@RequestMapping(value="/update", method = RequestMethod.POST)
	public ModelAndView updateUser(@ModelAttribute("userProfile") UserProfileEntity userProfile) {
        DateTime time = DateUtil.now();

        userProfilePreferenceService.updateProfile(userProfile);
        userProfile = userProfilePreferenceService.findById(userProfile.getId());
		ModelAndView modelAndView = populateModel(nextPage, userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

    @RequestMapping(value="/addExpenseType", method = RequestMethod.POST)
    public ModelAndView updateUser(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("expenseType") ExpenseTypeEntity expenseType, BindingResult result) {
        DateTime time = DateUtil.now();
        UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());

        expenseTypeValidator.validate(expenseType, result);
        if (result.hasErrors()) {
            ModelAndView modelAndView = populateModel(nextPage, userProfile);
            modelAndView.addObject("showTab", "#tabs-2");

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return modelAndView;
        }

        try {
            expenseType.setUserProfile(userSession.getUserProfileId());
            userProfilePreferenceService.addExpenseType(expenseType);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            result.rejectValue("expName", "", e.getLocalizedMessage());
        }

        ModelAndView modelAndView = populateModel(nextPage, userProfile);

        //There is UI logic based on this. Set the right to be active when responding.
        modelAndView.addObject("showTab", "#tabs-2");

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    @RequestMapping(value="/expenseTypeVisible", method = RequestMethod.GET)
    public ModelAndView changeExpenseTypeVisibleStatus(@RequestParam(value="uid") String profileId,
                                                       @RequestParam(value="id") String expenseTypId,
                                                       @RequestParam(value="status") String changeStatTo) {
        DateTime time = DateUtil.now();
        userProfilePreferenceService.modifyVisibilityOfExpenseType(expenseTypId, changeStatTo);
        UserProfileEntity userProfile = userProfilePreferenceService.findById(profileId);

        ModelAndView modelAndView = populateModel(nextPage, userProfile);

        //There is UI logic based on this. Set the right to be active when responding.
        modelAndView.addObject("showTab", "#tabs-2");

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

	/**
	 * @param nextPage
     * @param userProfile
	 * @return
	 */
	private ModelAndView populateModel(String nextPage, UserProfileEntity userProfile) {
        DateTime time = DateUtil.now();

        UserPreferenceEntity userPreference = userProfilePreferenceService.loadFromProfile(userProfile);
		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("userProfile", userProfile);
		modelAndView.addObject("userPreference", userPreference);
        modelAndView.addObject("expenseType", ExpenseTypeEntity.newInstance("", userProfile.getId()));


        List<ExpenseTypeEntity> expenseTypes = userProfilePreferenceService.allExpenseTypes(userProfile.getId());
        modelAndView.addObject("expenseTypes", expenseTypes);

        int count = 0;
        for(ExpenseTypeEntity expenseTypeEntity : expenseTypes) {
            if(expenseTypeEntity.isActive()) {
                count++;
            }
        }
        modelAndView.addObject("visibleExpenseTypes", count);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}
}
