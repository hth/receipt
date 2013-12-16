package com.receiptofi.web.controller;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.EmpLandingService;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 4/7/13
 * Time: 11:32 AM
 */
@Controller
@RequestMapping(value = "/emp")
@SessionAttributes({"userSession"})
public class EmpLandingController {
    private static final Logger log = LoggerFactory.getLogger(EmpLandingController.class);
    private static final String nextPage = "/emp/landing";

    @Autowired EmpLandingService empLandingService;

    @RequestMapping(value = "/landing", method = RequestMethod.GET)
    public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        ModelAndView modelAndView;
        if(userSession.getLevel() == UserLevelEnum.TECHNICIAN) {
            modelAndView = new ModelAndView(nextPage);

            //Note: findPending has to be before findUpdateWithLimit because records are update in the second query and this gets duplicates
            List<MessageDocumentEntity> pending = empLandingService.pendingReceipts(userSession.getEmailId(), userSession.getUserProfileId(), DocumentStatusEnum.OCR_PROCESSED);
            modelAndView.addObject("pending", pending);

            List<MessageDocumentEntity> queue = empLandingService.queuedReceipts(userSession.getEmailId(), userSession.getUserProfileId());
            modelAndView.addObject("queue", queue);

            List<MessageDocumentEntity> recheckPending = empLandingService.pendingReceipts(userSession.getEmailId(), userSession.getUserProfileId(), DocumentStatusEnum.TURK_REQUEST);
            modelAndView.addObject("recheckPending", recheckPending);

            List<MessageDocumentEntity> recheck = empLandingService.recheck(userSession.getEmailId(), userSession.getUserProfileId());
            modelAndView.addObject("recheck", recheck);
        }  else {
            //Re-direct user to his home page because user tried accessing UN-Authorized page
            log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
            modelAndView = new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
