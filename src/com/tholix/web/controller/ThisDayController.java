package com.tholix.web.controller;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ReceiptService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * User: hitender
 * Date: 5/12/13
 * Time: 1:23 AM
 */
@Controller
@RequestMapping(value = "/day")
@SessionAttributes({"userSession"})
public class ThisDayController {
    private static final Logger log = Logger.getLogger(ThisDayController.class);
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
