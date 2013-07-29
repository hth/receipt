/**
 *
 */
package com.tholix.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

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
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.ItemService;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ExpenseTypeForm;
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
    @Autowired private ItemService itemService;
    @Autowired private ExpenseTypeValidator expenseTypeValidator;

	@RequestMapping(value = "/i", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm) {
        DateTime time = DateUtil.now();

        UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());
        ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfile);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

    @RequestMapping(value="/i", method = RequestMethod.POST)
    public ModelAndView updateUser(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm, BindingResult result) {
        DateTime time = DateUtil.now();
        UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());

        expenseTypeValidator.validate(expenseTypeForm, result);
        if (result.hasErrors()) {
            ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfile);
            modelAndView.addObject("showTab", "#tabs-2");

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return modelAndView;
        }

        try {
            ExpenseTypeEntity expenseType = ExpenseTypeEntity.newInstance(expenseTypeForm.getExpName(), userSession.getUserProfileId());
            userProfilePreferenceService.addExpenseType(expenseType);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            result.rejectValue("expName", "", e.getLocalizedMessage());
        }

        ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfile);

        //There is UI logic based on this. Set the right to be active when responding.
        modelAndView.addObject("showTab", "#tabs-2");

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    /**
     * To Show and Hide the expense type
     * //TODO convert to ajax call instead
     *
     * @param expenseTypeId
     * @param changeStatTo
     * @param userSession
     * @return
     */
    @RequestMapping(value="/expenseTypeVisible", method = RequestMethod.GET)
    public ModelAndView changeExpenseTypeVisibleStatus(@ModelAttribute("userSession") UserSession userSession,
                                                       @RequestParam(value="id") String expenseTypeId,
                                                       @RequestParam(value="status") String changeStatTo,
                                                       @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm) {
        DateTime time = DateUtil.now();

        //Secondary check. In case some one tries to be smart by passing parameters in URL :)
        if(itemService.countItemsUsingExpenseType(expenseTypeId, userSession.getUserProfileId()) == 0) {
            userProfilePreferenceService.modifyVisibilityOfExpenseType(expenseTypeId, changeStatTo, userSession.getUserProfileId());
        }
        UserProfileEntity userProfile = userProfilePreferenceService.findById(userSession.getUserProfileId());

        ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfile);

        //There is UI logic based on this. Set the right to be active when responding.
        modelAndView.addObject("showTab", "#tabs-2");

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    /**
     * Only admin has access to this link. Others get 403 error.
     *
     * @param userSession
     * @param id
     * @param expenseTypeForm
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
	@RequestMapping(value = "/their", method = RequestMethod.GET)
	public ModelAndView getUser(@ModelAttribute("userSession") UserSession userSession,
                                @RequestParam("id") String id,
                                @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                HttpServletResponse httpServletResponse) throws IOException {

        DateTime time = DateUtil.now();

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.ADMIN.getValue()) {
                UserProfileEntity userProfile = userProfilePreferenceService.findById(id);
                ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfile);
                modelAndView.addObject("id", id);

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
                return modelAndView;
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
	}

    /**
     * Only Admin can update the user level. Others get 403 error. If the user cannot access /their, then its highly
     * unlikely to perform the action below.
     *
     * @param userSession
     * @param expenseTypeForm
     * @param userProfile
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
	@RequestMapping(value="/update", method = RequestMethod.POST)
	public ModelAndView updateUser(@ModelAttribute("userSession") UserSession userSession,
                                   @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                   @ModelAttribute("userProfile") UserProfileEntity userProfile,
                                   HttpServletResponse httpServletResponse) throws IOException {

        DateTime time = DateUtil.now();

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.ADMIN.getValue()) {
                userProfilePreferenceService.updateProfile(userProfile);
                userProfile = userProfilePreferenceService.findById(userProfile.getId());
                ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfile);

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
                return modelAndView;
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
	}

	/**
	 * @param nextPage
     * @param userProfile
	 * @return
	 */
	private ModelAndView populateModel(String nextPage, ExpenseTypeForm expenseTypeForm, UserProfileEntity userProfile) {
        DateTime time = DateUtil.now();

        UserPreferenceEntity userPreference = userProfilePreferenceService.loadFromProfile(userProfile);
		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("userProfile", userProfile);
		modelAndView.addObject("userPreference", userPreference);
        modelAndView.addObject("expenseTypeForm", expenseTypeForm);

        List<ExpenseTypeEntity> expenseTypes = userProfilePreferenceService.allExpenseTypes(userProfile.getId());
        modelAndView.addObject("expenseTypes", expenseTypes);

        Map<String, Long> expenseTypeCount = new HashMap<>();
        int count = 0;
        for(ExpenseTypeEntity expenseType : expenseTypes) {
            if(expenseType.isActive()) {
                count++;
            }

            expenseTypeCount.put(expenseType.getExpName(), itemService.countItemsUsingExpenseType(expenseType.getId(), userProfile.getId()));
        }
        modelAndView.addObject("expenseTypeCount", expenseTypeCount);
        modelAndView.addObject("visibleExpenseTypes", count);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}
}
