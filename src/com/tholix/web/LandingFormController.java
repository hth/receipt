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

import com.tholix.domain.ReceiptUser;

/**
 * @author hitender Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value = "/landing")
public class LandingFormController {
	protected final Log logger = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(@ModelAttribute("receiptUser") ReceiptUser receiptUser) {
		logger.info("LandingFormController loadForm: " + receiptUser.getEmailId());
		return "landing";
	}

}
