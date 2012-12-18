/**
 * 
 */
package com.tholix.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ReceiptUser;

/**
 * @author hitender
 * Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value="/landing")
public class LandingFormController {	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public String loadForm(Model model) {
		logger.info("LandingFormController loadForm: " + model.asMap().keySet());		
		return "landing";
	}

}
