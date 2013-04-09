/**
 * 
 */
package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserSession;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * @author hitender 
 * @when Jan 6, 2013 4:33:23 PM
 *
 */
@Controller
@RequestMapping(value = "/receiptpending")
public class ReceiptPendingController {
	private static final Logger log = Logger.getLogger(ReceiptPendingController.class);

	private String nextPage = "receiptpending";

	@Autowired private ReceiptOCRManager receiptOCRManager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(HttpSession session) {
        DateTime time = DateUtil.now();
        UserSession userSession = (UserSession) session.getAttribute("userSession");

		List<ReceiptEntityOCR> receipts = receiptOCRManager.getAllObjects(userSession.getUserProfileId());

		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("receipts", receipts);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

}
