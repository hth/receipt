/**
 *
 */
package com.receiptofi.web.controller;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import com.receiptofi.domain.ExpenseTypeEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.ExpenseTypeForm;
import com.receiptofi.web.form.UserProfilePreferenceForm;
import com.receiptofi.web.validator.ExpenseTypeValidator;

/**
 * Note: Follow PRG model with support for result binding
 *
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
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                 @ModelAttribute("userProfilePreferenceForm") UserProfilePreferenceForm userProfilePreferenceForm,
                                 SessionStatus sessionStatus,
                                 HttpServletResponse httpServletResponse,
                                 final Model model) throws IOException {
        DateTime time = DateUtil.now();

        UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());
        if(userProfile == null) {
            /** If user profile fails to load here then seems user was marked inactive */
            /** Sign off user */
            sessionStatus.setComplete();
            log.warn("User does not seem to have access granted: " + userSession.getEmailId());
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }

        userProfilePreferenceForm.setUserProfile(userProfile);
        ModelAndView modelAndView = populateModel(nextPage, null, userProfilePreferenceForm);

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.expenseTypeForm", model.asMap().get("result"));
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

    /**
     * Used for adding Expense Type
     *
     * Note: Gymnastic : The form that is being posted should be the last in order. Or else validation fails to work
     * @param userSession
     * @param userProfilePreferenceForm
     * @param expenseTypeForm
     * @param result
     * @return
     */
    @RequestMapping(value="/i", method = RequestMethod.POST)
    public String addExpenseTag(@ModelAttribute("userSession") UserSession userSession,
                                      @ModelAttribute("userProfilePreferenceForm") UserProfilePreferenceForm userProfilePreferenceForm,
                                      @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                      BindingResult result,
                                      final RedirectAttributes redirectAttrs) {
        DateTime time = DateUtil.now();

        //There is UI logic based on this. Set the right to be active when responding.
        redirectAttrs.addFlashAttribute("showTab", "#tabs-2");

        UserProfileEntity userProfile = userProfilePreferenceService.loadFromEmail(userSession.getEmailId());
        userProfilePreferenceForm.setUserProfile(userProfile);

        expenseTypeValidator.validate(expenseTypeForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");

            //Re-direct to prevent resubmit
            return "redirect:" + nextPage + "/i" + ".htm";
        }

        try {
            ExpenseTypeEntity expenseType = ExpenseTypeEntity.newInstance(expenseTypeForm.getExpName(), userSession.getUserProfileId());
            userProfilePreferenceService.addExpenseType(expenseType);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            result.rejectValue("expName", "", e.getLocalizedMessage());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());

        //Re-direct to prevent resubmit
        return "redirect:" + nextPage + "/i" + ".htm";
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
                                                       @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                                       @ModelAttribute("userProfilePreferenceForm") UserProfilePreferenceForm userProfilePreferenceForm) {
        DateTime time = DateUtil.now();

        //Secondary check. In case some one tries to be smart by passing parameters in URL :)
        if(itemService.countItemsUsingExpenseType(expenseTypeId, userSession.getUserProfileId()) == 0) {
            userProfilePreferenceService.modifyVisibilityOfExpenseType(expenseTypeId, changeStatTo, userSession.getUserProfileId());
        }
        UserProfileEntity userProfile = userProfilePreferenceService.findById(userSession.getUserProfileId());
        userProfilePreferenceForm.setUserProfile(userProfile);

        ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfilePreferenceForm);

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
                                @ModelAttribute("userProfilePreferenceForm") UserProfilePreferenceForm userProfilePreferenceForm,
                                HttpServletResponse httpServletResponse) throws IOException {

        DateTime time = DateUtil.now();

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.ADMIN.getValue()) {
                UserProfileEntity userProfile = userProfilePreferenceService.findById(id);
                userProfilePreferenceForm.setUserProfile(userProfile);
                ModelAndView modelAndView = populateModel(nextPage, expenseTypeForm, userProfilePreferenceForm);
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
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
	@RequestMapping(value="/update", method = RequestMethod.POST)
	public String updateUser(@ModelAttribute("userSession") UserSession userSession,
                                   @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                   @ModelAttribute("userProfilePreferenceForm") UserProfilePreferenceForm userProfilePreferenceForm,
                                   HttpServletResponse httpServletResponse) throws IOException {

        DateTime time = DateUtil.now();

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.ADMIN.getValue()) {
                UserProfileEntity userProfile = userProfilePreferenceService.findById(userProfilePreferenceForm.getUserProfile().getId());
                userProfile.setLevel(userProfilePreferenceForm.getUserProfile().getLevel());
                if(!userProfilePreferenceForm.isActive() || !userProfile.isActive()) {
                    if(userProfilePreferenceForm.isActive()) {
                        userProfile.active();
                    } else {
                        userProfile.inActive();
                    }
                }
                try {
                    //TODO remove this code as its a temporary fix to update existing email ids from capital case in the email to lowercase
                    //userProfile.setEmailId(StringUtils.lowerCase(userProfile.getEmailId()));

                    userProfilePreferenceService.updateProfile(userProfile);
                } catch (Exception exce) {
                    log.error("Failed updating User Profile: " + exce.getLocalizedMessage() + ", user profile Id: " + userProfile.getEmailId());
                    userProfilePreferenceForm.setErrorMessage("Failed updating user profile: " + exce.getLocalizedMessage());
                }

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
                //Re-direct to prevent resubmit
                return "redirect:" + nextPage + "/their" + ".htm?id=" + userProfilePreferenceForm.getUserProfile().getId();
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
     * @param userProfilePreferenceForm
	 * @return
	 */
	private ModelAndView populateModel(String nextPage, ExpenseTypeForm expenseTypeForm, UserProfilePreferenceForm userProfilePreferenceForm) {
        DateTime time = DateUtil.now();

        UserPreferenceEntity userPreference = userProfilePreferenceService.loadFromProfile(userProfilePreferenceForm.getUserProfile());
		ModelAndView modelAndView = new ModelAndView(nextPage);
        userProfilePreferenceForm.setUserPreference(userPreference);
        if(expenseTypeForm != null) {
            modelAndView.addObject("expenseTypeForm", expenseTypeForm);
        }

        List<ExpenseTypeEntity> expenseTypes = userProfilePreferenceService.allExpenseTypes(userProfilePreferenceForm.getUserProfile().getId());
        userProfilePreferenceForm.setExpenseTypes(expenseTypes);

        Map<String, Long> expenseTypeCount = new HashMap<>();
        int count = 0;
        for(ExpenseTypeEntity expenseType : expenseTypes) {
            if(expenseType.isActive()) {
                count++;
            }

            expenseTypeCount.put(
                    expenseType.getExpName(),
                    itemService.countItemsUsingExpenseType(expenseType.getId(), userProfilePreferenceForm.getUserProfile().getId())
            );
        }

        userProfilePreferenceForm.setExpenseTypeCount(expenseTypeCount);
        userProfilePreferenceForm.setVisibleExpenseTypes(count);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}
}
