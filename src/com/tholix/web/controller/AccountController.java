/**
 *
 */
package com.tholix.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.AccountService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.UserRegistrationForm;
import com.tholix.web.helper.AvailabilityStatus;
import com.tholix.web.validator.UserRegistrationValidator;

/**
 * @author hitender
 * @since Dec 24, 2012 3:13:26 PM
 *
 */
@Controller
@RequestMapping(value = "/new")
public class AccountController {
    private static final Logger log = Logger.getLogger(AccountController.class);

    private static final String NEW_ACCOUNT             = "/new";
    private static final String FORGOT_RECOVER_ACCOUNT  = "/forgot/recover";

    @Autowired private UserRegistrationValidator userRegistrationValidator;
    @Autowired private AccountService accountService;

	@ModelAttribute("userRegistrationForm")
	public UserRegistrationForm getUserRegistrationForm() {
		return UserRegistrationForm.newInstance();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm() {
		log.debug("Loading New Account");
		return NEW_ACCOUNT;
	}

	@RequestMapping(method = RequestMethod.POST, params = {"signup"})
	public String post(@ModelAttribute("userRegistrationForm") UserRegistrationForm userRegistrationForm, BindingResult result, final RedirectAttributes redirectAttrs) {
        DateTime time = DateUtil.now();
        userRegistrationValidator.validate(userRegistrationForm, result);
		if(result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "validation error");
            return NEW_ACCOUNT;
		}

        UserProfileEntity userProfile = accountService.findIfUserExists(userRegistrationForm.getEmailId());
        if(userProfile != null) {
            userRegistrationValidator.accountExists(userRegistrationForm, result);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "account exists");
            return NEW_ACCOUNT;
        }

        try {
            //TODO For now de-activate all registration. Currently registration is by invitation only.
            userProfile = accountService.createNewAccount(userRegistrationForm);
            log.info("Registered new Email Id: " + userProfile.getEmailId());
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "failure in registering user");
            return NEW_ACCOUNT;
        }

        UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId(), userProfile.getLevel());
        redirectAttrs.addFlashAttribute("userSession", userSession);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");

        if(userProfile.isActive()) {
            /** This code to invoke the controller */
            return "redirect:/landing.htm";
        } else {
            //TODO For now de-activate all registration. Currently registration is by invitation only.
            return NEW_ACCOUNT;
        }
    }

    /**
     * Starts the account recovery process
     *
     * @param userRegistrationForm
     * @param result
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"recover"})
    public String recover(@ModelAttribute("userRegistrationForm") UserRegistrationForm userRegistrationForm,
                          @SuppressWarnings("unused") BindingResult result, final RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("userRegistrationForm", userRegistrationForm);
        return "redirect:" + FORGOT_RECOVER_ACCOUNT + ".htm";
    }

    /**
     * Ajax call to check if the account is available to register.
     *
     * Note: This code can be accessed from outside without any checking. Mostly will provide information about user is
     *        registered in the system or not.
     *
     *  TODO: Change it. This is currently a threat.
     *
     * @param emailId
     * @return
     */
    @RequestMapping(value="/availability", method=RequestMethod.GET)
    public @ResponseBody
    String getAvailability(@RequestParam String emailId) {
        DateTime time = DateUtil.now();
        log.info("Auto find if the emailId is present: " + emailId);
        AvailabilityStatus availabilityStatus;

        UserProfileEntity userProfileEntity = accountService.findIfUserExists(emailId);
        if(userProfileEntity != null) {
            if (userProfileEntity.getEmailId().equals(emailId)) {
                log.info("Not Available: " + emailId);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
                availabilityStatus = AvailabilityStatus.notAvailable(emailId);
                return new StringBuilder()
                        .append("{ \"valid\" : \"")
                        .append(availabilityStatus.isAvailable())
                        .append("\", \"message\" : \"")
                        .append("<b>")
                        .append(emailId)
                        .append("</b>")
                        .append(" is already registered. ")
                        .append(StringUtils.join(availabilityStatus.getSuggestions()))
                        .append("\" }")
                        .toString();
            }
        }
        log.info("Available: " + emailId);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
        availabilityStatus = AvailabilityStatus.available();
        return "{ \"valid\" : \"" + availabilityStatus.isAvailable() + "\" }";
    }
}
