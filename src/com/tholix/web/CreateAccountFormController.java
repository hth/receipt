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

import com.tholix.domain.UserRegistrationWrapper;
import com.tholix.domain.UserEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserManager;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UserRegistrationValidator;

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
	@Qualifier("userManager")
	private UserManager userManager;

	@Autowired
	private UserProfileManager userProfileManager;

	@Autowired
	private UserPreferenceManager userPreferenceManager;
	
	@Autowired
	private UserRegistrationValidator userRegistrationValidator;

	@ModelAttribute("userRegistrationWrapper")
	public UserRegistrationWrapper getUserRegistrationWrapper() {
		return UserRegistrationWrapper.newInstance();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		log.debug("Loading New Account");
		return "newaccount";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("userRegistrationWrapper") UserRegistrationWrapper userRegistrationWrapper, BindingResult result, final RedirectAttributes redirectAttrs) {
		//TODO remove the next three lines
		userManager.dropCollection();
		userProfileManager.dropCollection();
		userPreferenceManager.dropCollection();
		
		userRegistrationValidator.validate(userRegistrationWrapper, result);
		if (result.hasErrors()) {
			return "newaccount";
		} else {
			UserEntity user;
			UserProfileEntity userProfile;
			try {
				user = userRegistrationWrapper.newUserEntity();
				userManager.saveObject(user);
			} catch (Exception e) {
				log.error("During saving UserEntity: " + e.getLocalizedMessage());
				result.rejectValue("emailId", "field.emailId.duplicate");
				return "newaccount";
			}

			try {
				userProfileManager.saveObject(userRegistrationWrapper.newUserProfileEntity(user));
				userProfile = userProfileManager.getObject(user);
			} catch (Exception e) {
				log.error("During saving UserProfileEntity: " + e.getLocalizedMessage());
				return "newaccount";
			}

			try {
				userPreferenceManager.saveObject(userRegistrationWrapper.newUserPreferenceEntity(userProfile));
			} catch (Exception e) {
				log.error("During saving UserPreferenceEntity: " + e.getLocalizedMessage());
				return "newaccount";
			}

			log.info("Registered new Email Id: " + userProfile.getEmailId());
			
			UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId());					
			redirectAttrs.addFlashAttribute("userSession", userSession);
			
			return "redirect:/landing.htm";
		}
	}

}
