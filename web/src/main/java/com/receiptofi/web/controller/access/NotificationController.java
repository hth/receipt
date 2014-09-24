package com.receiptofi.web.controller.access;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.repository.NotificationManager;
import com.receiptofi.service.NotificationService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.form.NotificationForm;
import com.receiptofi.web.util.PerformanceProfiling;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:51 PM
 */
@Controller
@RequestMapping(value = "/access/notification")
public final class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    @Autowired private NotificationService notificationService;

    /**
     * maps to notification.jsp
     */
    @Value("${next.page:/notification}")
    private String nextPage;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadForm() {
        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("LandingController loadForm: " + receiptUser.getRid());

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject(
                "notificationForm",
                NotificationForm.newInstance(notificationService.notifications(receiptUser.getRid(), NotificationManager.ALL))
        );

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
