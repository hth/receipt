/**
 * 
 */
package com.tholix.web;

import java.util.Date;

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

import com.tholix.domain.ReceiptUser;
import com.tholix.domain.UserProfile;
import com.tholix.service.ReceiptUserManager;
import com.tholix.service.ReceiptUserValidator;
import com.tholix.service.UserProfileManager;

/**
 * @author hitender 
 * @when Dec 16, 2012 6:12:17 PM
 */
@Controller
@RequestMapping(value = "/login")
public class LoginFormController {
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	@Qualifier("receiptUserManager")
	private ReceiptUserManager receiptUserManager;
	
	@Autowired
	private UserProfileManager userProfileManager;

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		logger.info("LoginFormController login");
		model.addAttribute("receiptUser", ReceiptUser.findReceiptUser(""));
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute ReceiptUser receiptUser, BindingResult result, final RedirectAttributes redirectAttrs) {
		logger.info("post");
		new ReceiptUserValidator().validate(receiptUser, result);
		if (result.hasErrors()) {
			return "login";
		} else {
			receiptUserManager.saveObject(receiptUser);
			ReceiptUser found = receiptUserManager.getObject(receiptUser.getEmailId());
			
			UserProfile userProfile = UserProfile.newInstance("Tom", "Shawn", new Date(), found);
			userProfileManager.saveObject(userProfile);
			
			logger.info("Email Id: " + receiptUser.getEmailId() + " and found " + found.getEmailId());
			redirectAttrs.addFlashAttribute("receiptUser", receiptUser);
			return "redirect:/landing.htm";
		}
	}
}
