/**
 *
 */
package com.receiptofi.web.controller;

import com.receiptofi.domain.UserSession;
import com.receiptofi.service.DocumentPendingService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.PendingReceiptForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

/**
 * @author hitender
 * @since Jan 6, 2013 4:33:23 PM
 *
 */
@Controller
@RequestMapping(value = "/pending")
@SessionAttributes({"userSession"})
public class DocumentPendingController {
	private static final Logger log = LoggerFactory.getLogger(DocumentPendingController.class);

	private String RECEIPT_PENDING = "/pending";

	@Autowired private DocumentPendingService documentPendingService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("pendingReceiptForm") PendingReceiptForm pendingReceiptForm) {
        DateTime time = DateUtil.now();

		documentPendingService.getAllPending(userSession.getUserProfileId(), pendingReceiptForm);
        documentPendingService.getAllRejected(userSession.getUserProfileId(), pendingReceiptForm);

		ModelAndView modelAndView = new ModelAndView(RECEIPT_PENDING);
		modelAndView.addObject("pendingReceiptForm", pendingReceiptForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

}
