package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.UserSession;
import com.tholix.service.routes.MessageManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * User: hitender
 * Date: 4/7/13
 * Time: 11:32 AM
 */
@Controller
@RequestMapping(value = "/emp")
public class EmpLandingController extends BaseController {
    private static final Logger log = Logger.getLogger(EmpLandingController.class);
    private static final String nextPage = "/emp/landing";

    @Autowired private MessageManager messageManager;

    @RequestMapping(value = "/landing", method = RequestMethod.GET)
    public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, HttpSession session) {
        DateTime time = DateUtil.now();
        userSession = isSessionSet(userSession, session);
        ModelAndView modelAndView = new ModelAndView(nextPage);

        List<MessageReceiptEntityOCR> queue = messageManager.findUpdateWithLimit(userSession.getEmailId(), userSession.getUserProfileId());
        modelAndView.addObject("queue", queue);

        List<MessageReceiptEntityOCR> pending = messageManager.findPending(userSession.getEmailId(), userSession.getUserProfileId());
        modelAndView.addObject("pending", pending);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
