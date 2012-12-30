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

import com.tholix.domain.UserEntity;
import com.tholix.service.UserManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.NewUserValidator;
import com.tholix.service.validator.UserValidator;
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
	@Qualifier("userManager")
	private UserManager userManager;

	@Autowired
	private UserProfileManager userProfileManager;
	
	@Autowired
	private UserValidator userValidator;

	/**
	 * @link http://stackoverflow.com/questions/1069958/neither-bindingresult-nor-plain-target-object-for-bean-name-available-as-request
	 * 
	 * @info: OR you could just replace it in Form Request method getReceiptUser 
	 *        model.addAttribute("receiptUser", UserEntity.findReceiptUser(""));
	 * 
	 * @return UserEntity
	 */
	@ModelAttribute("user")
	public UserEntity getUser() {
		return UserEntity.findUser("");
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		log.info("LoginFormController login");
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("user") UserEntity user, BindingResult result, final RedirectAttributes redirectAttrs) {
		userValidator.validate(user, result);
		if (result.hasErrors()) {
			return "login";
		} else {
			UserEntity found = userManager.getObject(user.getEmailId());
			if (found != null) {
				user.setPassword(SHAHashing.hashCode(user.getPassword()));
				if (found.equals(user)) {
					log.info("Email Id: " + user.getEmailId() + " and found " + found.getEmailId());
					redirectAttrs.addFlashAttribute("user", found);
					return "redirect:/landing.htm";
				} else {
					result.rejectValue("emailId", "field.emailId.notMatching");
					return "login";
				}
			} else {
				result.rejectValue("emailId", "field.emailId.notFound");
				return "login";
			}
		}
	}
}
