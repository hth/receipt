package com.receiptofi.web.controller.access;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 5/12/13
 * Time: 1:23 AM
 */
@Controller
@RequestMapping(value = "/day")
@SessionAttributes({"userSession"})
public class ThisDayController {
    private static final Logger log = LoggerFactory.getLogger(ThisDayController.class);
    private static final String nextPage = "/day";

    @Autowired private ReceiptService receiptService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getThisDay(@ModelAttribute("userSession") UserSession userSession, @RequestParam("date") String date) {
        DateTime time = DateUtil.now();

        Long longDate = Long.parseLong(date);
        DateTime dateTime = new DateTime(longDate);
        List<ReceiptEntity> receipts = receiptService.findReceipt(dateTime, userSession.getUserProfileId());

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("receipts", receipts);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
