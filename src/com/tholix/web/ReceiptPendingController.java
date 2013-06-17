/**
 *
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserSession;
import com.tholix.service.ReceiptPendingService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.PendingReceiptForm;

/**
 * @author hitender
 * @since Jan 6, 2013 4:33:23 PM
 *
 */
@Controller
@RequestMapping(value = "/pending")
@SessionAttributes({"userSession"})
public class ReceiptPendingController {
	private static final Logger log = Logger.getLogger(ReceiptPendingController.class);

	private String RECEIPT_PENDING = "/pending";

	@Autowired private ReceiptPendingService receiptPendingService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
		List<PendingReceiptForm> pendingReceiptFormList = receiptPendingService.getAllPending(userSession.getUserProfileId());

		ModelAndView modelAndView = new ModelAndView(RECEIPT_PENDING);
		modelAndView.addObject("pendingReceipts", pendingReceiptFormList);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

}
