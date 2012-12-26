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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.ReceiptUserEntity;
import com.tholix.domain.UserProfileEntity;
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
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	@Qualifier("receiptUserManager")
	private ReceiptUserManager receiptUserManager;
	
	@Autowired
	private UserProfileManager userProfileManager;
	
	/** 
	 * @link http://stackoverflow.com/questions/1069958/neither-bindingresult-nor-plain-target-object-for-bean-name-available-as-request
	 * 
	 * Info: OR you could just replace it in Form Request method getReceiptUser
	 * model.addAttribute("receiptUser", ReceiptUserEntity.findReceiptUser(""));
	 * 
	 * @return ReceiptUserEntity
	 */
	@ModelAttribute("receiptUser")
	public ReceiptUserEntity getReceiptUser() {
		return ReceiptUserEntity.findReceiptUser("");
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(Model model) {
		log.info("LoginFormController login");		
		return "login";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("receiptUser") ReceiptUserEntity receiptUser, BindingResult result, final RedirectAttributes redirectAttrs) {
		new ReceiptUserValidator().validate(receiptUser, result);
		if (result.hasErrors()) {
			return "login";
		} else {
			ReceiptUserEntity found = receiptUserManager.getObject(receiptUser.getEmailId());
			if(found != null) {
				if(found.equals(receiptUser)) {
					log.info("Email Id: " + receiptUser.getEmailId() + " and found " + found.getEmailId());
					redirectAttrs.addFlashAttribute("receiptUser", found);
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
