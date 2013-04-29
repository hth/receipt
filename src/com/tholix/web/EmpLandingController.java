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

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.EmpLandingService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * User: hitender
 * Date: 4/7/13
 * Time: 11:32 AM
 */
@Controller
@RequestMapping(value = "/emp")
@SessionAttributes({"userSession"})
public class EmpLandingController {
    private static final Logger log = Logger.getLogger(EmpLandingController.class);
    private static final String nextPage = "/emp/landing";

    @Autowired EmpLandingService empLandingService;

    @RequestMapping(value = "/landing", method = RequestMethod.GET)
    public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        ModelAndView modelAndView;
        if(userSession.getLevel() == UserLevelEnum.WORKER) {
            modelAndView = new ModelAndView(nextPage);

            //Note: findPending has to be before findUpdateWithLimit because records are update in the second query and this gets duplicates
            List<MessageReceiptEntityOCR> pending = empLandingService.pendingReceipts(userSession.getEmailId(), userSession.getUserProfileId());
            modelAndView.addObject("pending", pending);

            List<MessageReceiptEntityOCR> queue = empLandingService.queuedReceipts(userSession.getEmailId(), userSession.getUserProfileId());
            modelAndView.addObject("queue", queue);
        }  else {
            //Re-direct user to his home page because user tried accessing UN-Authorized page
            log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
            modelAndView = new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
