/**
 * 
 */
package com.tholix.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.tholix.domain.ReceiptUser;
import com.tholix.service.ReceiptUserValidator;

/**
 * @author hitender 
 * Dec 16, 2012 6:12:17 PM
 */ 
@Controller
@RequestMapping(value="/login")
@SessionAttributes(types = ReceiptUser.class)
public class LoginFormController {	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public String loadForm(Model model) {
		logger.info("LoginFormController login");
		model.addAttribute("receiptUser", ReceiptUser.getReceiptUserInstance());
		return "login";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView post(@ModelAttribute ReceiptUser receiptUser, BindingResult result, SessionStatus status) {
		logger.info("post");
		new ReceiptUserValidator().validate(receiptUser, result);
		if (result.hasErrors()) {
			return new ModelAndView("login");
		}
		else {
			logger.info("Email Id: " + receiptUser.getEmailId());
			status.setComplete();			
			
			Map<String, Object> model = new HashMap<String, Object>();
     		model.put("receiptUserId", receiptUser.getEmailId());
    		model.put("now", new Date().toString());
    		
    		return new ModelAndView("redirect:/landing.htm", "model", model);
		}
	}
	
	

//	private ReceiptUser receiptUser;
//
////	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
////		logger.info("LoginFormController login");	
////		
////		Map<String, Object> model = new HashMap<String, Object>();
////		model.put("receiptUser", ReceiptUser.getReceiptUserInstance());
////		
////		return new ModelAndView("login", "model", model);
////	}
//	
////	public ModelAndView onSubmit(Object command) throws ServletException {
////		logger.info("form submitted");	
////		String emailId = ((ReceiptUser) command).getEmailId();
////		logger.info("emailId: " + emailId);
////		
////		Map<String, Object> model = new HashMap<String, Object>();
////		model.put("receiptUserId", emailId);
////		model.put("now", new Date().toString());
////		
////		return new ModelAndView(new RedirectView(getSuccessView()), "model", model);
////	}
//	
////	protected Object formBackingObject(HttpServletRequest request) throws ServletException {
////		logger.info("formBackingObject");
////        //return ReceiptUser.getReceiptUserForSignup("test@test.com", "");
////		return ReceiptUser.getReceiptUserInstance();
////    }
////
//	public ReceiptUser getReceiptUser() {
//		return receiptUser;
//	}
//
//	public void setReceiptUser(ReceiptUser receiptUser) {
//		this.receiptUser = receiptUser;
//	}
}
