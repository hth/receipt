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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UserLoginValidator;
import com.tholix.utils.SHAHashing;
import com.tholix.web.form.UserLoginForm;

/**
 * @author hitender
 * @when Dec 16, 2012 6:12:17 PM
 */
@Controller
@RequestMapping(value = "/login")
public class LoginFormController {
	private static final Logger log = Logger.getLogger(LoginFormController.class);

	@Autowired
	@Qualifier("userAuthenticationManager")
	private UserAuthenticationManager userAuthenticationManager;

	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserLoginValidator userLoginValidator;

	// TODO add later to my answer http://stackoverflow.com/questions/3457134/how-to-display-a-formatted-datetime-in-spring-mvc-3-0

	/**
	 * @link http://stackoverflow.com/questions/1069958/neither-bindingresult-nor-plain-target-object-for-bean-name-available-as-request
	 * 
	 * @info: OR you could just replace it in Form Request method getReceiptUser model.addAttribute("receiptUser", UserAuthenticationEntity.findReceiptUser(""));
	 * 
	 * @return UserAuthenticationEntity
	 */
	@ModelAttribute("userLoginForm")
	public UserLoginForm getUserLoginForm() {
		return UserLoginForm.newInstance();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		log.info("LoginFormController login");
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("userLoginForm") UserLoginForm userLoginForm, BindingResult result, final RedirectAttributes redirectAttrs) {
		userLoginValidator.validate(userLoginForm, result);
		if (result.hasErrors()) {
			return "login";
		} else {
			UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail(userLoginForm.getEmailId());
			if (userProfile != null) {
				userLoginForm.setPassword(SHAHashing.hashCode(userLoginForm.getPassword()));
				UserAuthenticationEntity user = userAuthenticationManager.findOne(userProfile.getUserAuthentication().getId());
				if (user.getPassword().equals(userLoginForm.getPassword())) {
					log.info("Email Id: " + userLoginForm.getEmailId() + " and found " + userProfile.getEmailId());

					UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId(), userProfile.getLevel());
					redirectAttrs.addFlashAttribute("userSession", userSession);

					String path = "redirect:/landing.htm";
					switch(userProfile.getLevel()) {
						case ADMIN:
							path = "redirect:/admin/landing.htm";
							break;
						case USER_PAID:
							//do nothing for now
							break;
						case USER:
							//do nothing for now
							break;
						case EMPLOYER:
							//do nothing for now
							break;
						case EMPLOYER_PAID:
							//do nothing for now
							break;
						case WORKER:
							//do nothing for now
							break;
						case SUPERVISOR:
							//do nothing for now
							break;
					}								
					return path;
				} else {
					userLoginForm.setPassword("");
					log.error("Password not matching for user : " + userLoginForm.getEmailId());
					result.rejectValue("emailId", "field.emailId.notMatching");
					return "login";
				}
			} else {
				userLoginForm.setPassword("");
				log.error("No Email Id found in record : " + userLoginForm.getEmailId());
				result.rejectValue("emailId", "field.emailId.notFound");
				return "login";
			}
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

	public void setUserLoginValidator(UserLoginValidator userLoginValidator) {
		this.userLoginValidator = userLoginValidator;
	}
}
