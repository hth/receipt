/**
 * 
 */
package com.tholix.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tholix.domain.ReceiptUserEntity;

/**
 * @author hitender 
 * @when Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value = "/landing")
public class LandingFormController {
	private final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(@ModelAttribute("receiptUser") ReceiptUserEntity receiptUser) {
		log.info("LandingFormController loadForm: " + receiptUser.getEmailId());
		return "landing";
	}

}
