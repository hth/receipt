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
import com.tholix.domain.UserLoginWrapper;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UserLoginValidator;
import com.tholix.utils.SHAHashing;

/**
 * @author hitender
 * @when Dec 16, 2012 6:12:17 PM
 */
@Controller
@RequestMapping(value = "/login")
public class LoginFormController {
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	@Qualifier("userAuthenticationManager")
	private UserAuthenticationManager userAuthenticationManager;

	@Autowired
	private UserProfileManager userProfileManager;

	@Autowired
	private UserLoginValidator userLoginValidator;

	// TODO add later to my answer http://stackoverflow.com/questions/3457134/how-to-display-a-formatted-datetime-in-spring-mvc-3-0

	/**
	 * @link http://stackoverflow.com/questions/1069958/neither-bindingresult-nor-plain-target-object-for-bean-name-available-as-request
	 * 
	 * @info: OR you could just replace it in Form Request method getReceiptUser model.addAttribute("receiptUser", UserAuthenticationEntity.findReceiptUser(""));
	 * 
	 * @return UserAuthenticationEntity
	 */
	@ModelAttribute("userLoginWrapper")
	public UserLoginWrapper getUserLoginWrapper() {
		return UserLoginWrapper.newInstance();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		log.info("LoginFormController login");
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("userLoginWrapper") UserLoginWrapper userLoginWrapper, BindingResult result, final RedirectAttributes redirectAttrs) {
		userLoginValidator.validate(userLoginWrapper, result);
		if (result.hasErrors()) {
			return "login";
		} else {
			UserProfileEntity userProfile = userProfileManager.getObjectUsingEmail(userLoginWrapper.getEmailId());
			if (userProfile != null) {
				userLoginWrapper.setPassword(SHAHashing.hashCode(userLoginWrapper.getPassword()));
				UserAuthenticationEntity user = userAuthenticationManager.getObject(userProfile.getUserAuthentication().getId());
				if (user.getPassword().equals(userLoginWrapper.getPassword())) {
					log.info("Email Id: " + userLoginWrapper.getEmailId() + " and found " + userProfile.getEmailId());

					UserSession userSession = UserSession.newInstance(userProfile.getEmailId(), userProfile.getId());
					redirectAttrs.addFlashAttribute("userSession", userSession);

					return "redirect:/landing.htm";
				} else {
					log.error("Password not matching for user : " + userLoginWrapper.getEmailId());
					result.rejectValue("emailId", "field.emailId.notMatching");
					return "login";
				}
			} else {
				log.error("No Email Id found in record : " + userLoginWrapper.getEmailId());
				result.rejectValue("emailId", "field.emailId.notFound");
				return "login";
			}
		}
	}
}
