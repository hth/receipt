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

import com.tholix.domain.NewUserWrapper;
import com.tholix.domain.UserEntity;
import com.tholix.service.UserManager;
import com.tholix.service.UserPreferenceManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.NewUserValidator;

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

	/** For Drop down */
	// @ModelAttribute("accountTypeMap")
	// public Map<String, String> populateAccountTypeMap() {
	// Map<String, String> result = new HashMap<String, String>();
	// for (AccountTypeEnum accountType : AccountTypeEnum.values()) {
	// result.put(accountType.getName(), accountType.getVisibleName());
	// }
	// return result;
	// }

	@ModelAttribute("newUserWrapper")
	public NewUserWrapper getNewUserWrapper() {
		return NewUserWrapper.newInstance();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		log.debug("Loading New Account");
		return "newaccount";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("newUserWrapper") NewUserWrapper newUserWrapper, BindingResult result, final RedirectAttributes redirectAttrs) {
		new NewUserValidator().validate(newUserWrapper, result);
		if (result.hasErrors()) {
			return "newaccount";
		} else {
			UserEntity user;
			try {
				userManager.saveObject(newUserWrapper.newReceiptUserEntity());
				user = userManager.getObject(newUserWrapper.getEmailId());
			} catch (Exception e) {
				result.rejectValue("emailId", "field.emailId.duplicate");
				return "newaccount";
			}

			try {
				userProfileManager.saveObject(newUserWrapper.newUserProfileEntity(user));
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				return "newaccount";
			}

			try {
				userPreferenceManager.saveObject(newUserWrapper.newUserPreferenceEntity(user));
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				return "newaccount";
			}

			log.info("Registered new Email Id: " + user.getEmailId());
			redirectAttrs.addFlashAttribute("user", user);
			return "redirect:/landing.htm";
		}
	}

}
