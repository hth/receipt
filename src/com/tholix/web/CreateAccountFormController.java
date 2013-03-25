/**
 * 
 */
package com.tholix.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UserRegistrationValidator;
import com.tholix.web.form.UserRegistrationForm;

/**
 * @author hitender
 * @when Dec 24, 2012 3:13:26 PM
 * 
 */
@Controller
@RequestMapping(value = "/newaccount")
public class CreateAccountFormController {
	private final Log log = LogFactory.getLog(getClass());

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
		return "newaccount";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("userRegistrationForm") UserRegistrationForm userRegistrationForm, BindingResult result, final RedirectAttributes redirectAttrs) {
		userRegistrationValidator.validate(userRegistrationForm, result);
		if (result.hasErrors()) {
			return "newaccount";
		} else {
			UserAuthenticationEntity userAuthentication;
			UserProfileEntity userProfile;
			try {
				userAuthentication = userRegistrationForm.newUserAuthenticationEntity();
				userAuthenticationManager.saveObject(userAuthentication);
			} catch (Exception e) {
				log.error("During saving UserAuthenticationEntity: " + e.getLocalizedMessage());
				result.rejectValue("emailId", "field.emailId.duplicate");
				return "newaccount";
			}

			try {
				userProfile = userRegistrationForm.newUserProfileEntity(userAuthentication);
				userProfileManager.saveObject(userProfile);
			} catch (Exception e) {
				log.error("During saving UserProfileEntity: " + e.getLocalizedMessage());
				return "newaccount";
			}

			try {
				userPreferenceManager.saveObject(userRegistrationForm.newUserPreferenceEntity(userProfile));
			} catch (Exception e) {
				log.error("During saving UserPreferenceEntity: " + e.getLocalizedMessage());
				return "newaccount";
			}

			log.info("Registered new Email Id: " + userProfile.getEmailId());

			UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId());
			redirectAttrs.addFlashAttribute("userSession", userSession);

			/** This code to invoke the controller */
			return "redirect:/landing.htm";
		}
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
