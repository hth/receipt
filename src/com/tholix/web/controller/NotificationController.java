package com.tholix.web.controller;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.UserSession;
import com.tholix.repository.NotificationManager;
import com.tholix.service.NotificationService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.NotificationForm;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:51 PM
 */
@Controller
@RequestMapping(value = "/notification")
@SessionAttributes({"userSession"})
public class NotificationController {
    private static final Logger log = Logger.getLogger(LandingController.class);

    @Autowired NotificationService notificationService;

    /**
     * Refers to landing.jsp
     */
    private static final String NEXT_PAGE_IS_CALLED_NOTIFICATION = "/notification";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("notificationForm") NotificationForm notificationForm) {
        DateTime time = DateUtil.now();
        log.info("LandingController loadForm: " + userSession.getEmailId());

        ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_NOTIFICATION);
        notificationForm.setNotifications(notificationService.notifications(userSession.getUserProfileId(), NotificationManager.ALL));

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
