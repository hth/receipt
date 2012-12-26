/**
 * 
 */
package com.tholix.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes; 

import com.tholix.domain.ReceiptUserEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.service.ReceiptUserManager;
import com.tholix.service.UserProfileManager;

/**
 * @author hitender 
 * @when Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value = "/landing") 
public class LandingFormController {
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private UserProfileManager userProfileManager;	
	

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(@ModelAttribute("receiptUser") ReceiptUserEntity receiptUserEntity) {
		log.info("LandingFormController loadForm: " + receiptUserEntity.getEmailId());
		UserProfileEntity userProfileEntity = userProfileManager.getObject(receiptUserEntity);
		log.info(userProfileEntity.getName());
		return "landing";		
	}
}
