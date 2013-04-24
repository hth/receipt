/**
 *
 */
package com.tholix.web;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;
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

	@Autowired
	@Qualifier("userAuthenticationManager")
	private UserAuthenticationManager userAuthenticationManager;
	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserPreferenceManager userPreferenceManager;
	@Autowired private UserRegistrationValidator userRegistrationValidator;

	@ModelAttribute("userRegistrationForm")
	public UserRegistrationForm getUserRegistrationForm() {
		return UserRegistrationForm.newInstance();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
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
		} else {
            UserAuthenticationEntity userAuthentication;
            UserProfileEntity userProfile = userProfileManager.findOneByEmail(userRegistrationForm.getEmailId());

            if(userProfile != null) {
                userRegistrationValidator.accountExists(userRegistrationForm, result);
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "account exists");
                return NEW_ACCOUNT;
            }

			try {
				userAuthentication = userRegistrationForm.newUserAuthenticationEntity();
				userAuthenticationManager.save(userAuthentication);
			} catch (Exception e) {
				log.error("During saving UserAuthenticationEntity: " + e.getLocalizedMessage());
				result.rejectValue("emailId", "field.emailId.duplicate");
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user authentication");
                return NEW_ACCOUNT;
			}

			try {
				userProfile = userRegistrationForm.newUserProfileEntity(userAuthentication);
				userProfileManager.save(userProfile);
			} catch (Exception e) {
				log.error("During saving UserProfileEntity: " + e.getLocalizedMessage());
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user profile");
                return NEW_ACCOUNT;
			}

			try {
				userPreferenceManager.save(userRegistrationForm.newUserPreferenceEntity(userProfile));
			} catch (Exception e) {
				log.error("During saving UserPreferenceEntity: " + e.getLocalizedMessage());
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error saving user preference");
                return NEW_ACCOUNT;
			}

			log.info("Registered new Email Id: " + userProfile.getEmailId());

			UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId(), userProfile.getLevel());
			redirectAttrs.addFlashAttribute("userSession", userSession);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
            /** This code to invoke the controller */
			return "redirect:/landing.htm";
		}
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
        UserProfileEntity userProfileEntity = userProfileManager.findOneByEmail(emailId);
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

	/**
	 * Setters below are used by JUnit
	 */

	public void setUserAuthenticationManager(UserAuthenticationManager userAuthenticationManager) {
		this.userAuthenticationManager = userAuthenticationManager;
	}

	public void setUserProfileManager(UserProfileManager userProfileManager) {
		this.userProfileManager = userProfileManager;
	}

	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager) {
		this.userPreferenceManager = userPreferenceManager;
	}

	public void setUserRegistrationValidator(UserRegistrationValidator userRegistrationValidator) {
		this.userRegistrationValidator = userRegistrationValidator;
	}
}
