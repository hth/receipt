/**
 *
 */
package com.tholix.web;

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
 * @when Dec 24, 2012 3:13:26 PM
 *
 */
@Controller
@RequestMapping(value = "/new")
public class AccountController {
	private static final Logger log = Logger.getLogger(AccountController.class);
    private static final String NEW_ACCOUNT = "new";

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

	@RequestMapping(method = RequestMethod.POST, params = {"Signup"})
	public String post(@ModelAttribute("userRegistrationForm") UserRegistrationForm userRegistrationForm, BindingResult result, final RedirectAttributes redirectAttrs) {
        DateTime time = DateUtil.now();
        userRegistrationValidator.validate(userRegistrationForm, result);
		if (result.hasErrors()) {
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
        /** This code to invoke the controller */
        return "redirect:/landing.htm";
	}

    //TODO
    @RequestMapping(method = RequestMethod.POST, params = {"Recover"})
    public String post() {
        log.warn("Recover method clicked. To be implemented");
        return NEW_ACCOUNT;
    }

    @RequestMapping(value="/availability", method=RequestMethod.GET)
    public @ResponseBody
    AvailabilityStatus getAvailability(@RequestParam String emailId) {
        DateTime time = DateUtil.now();
        log.info("Auto find if the emailId is present: " + emailId);
        UserProfileEntity userProfileEntity = accountService.findIfUserExists(emailId);
        if(userProfileEntity != null) {
            if (userProfileEntity.getEmailId().equals(emailId)) {
                log.info("Not Available: " + emailId);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
                return AvailabilityStatus.notAvailable(emailId);
            }
        }
        log.info("Available: " + emailId);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
        return AvailabilityStatus.available();
    }
}
