package com.receiptofi.web.controller.access;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.repository.NotificationManager;
import com.receiptofi.service.NotificationService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.form.NotificationDetailForm;
import com.receiptofi.web.form.NotificationForm;
import com.receiptofi.web.util.PerformanceProfiling;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:51 PM
 */
@Controller
@RequestMapping (value = "/access")
public final class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    @Autowired private NotificationService notificationService;

    /**
     * maps to notification.jsp
     */
    @Value ("${next.page:/notification}")
    private String nextPage;

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/notification",
            method = RequestMethod.GET
    )
    public ModelAndView loadForm() {
        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("LandingController loadForm: " + receiptUser.getRid());

        List<NotificationEntity> notifications = notificationService.getAllNotifications(
                receiptUser.getRid(),
                NotificationManager.ALL
        );

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("notificationForm", NotificationForm.newInstance(notifications.size(), notifications));
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/notificationPaginated/{current}",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public List<String> paginatedNotification(
            @PathVariable ("current")
            int current
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NotificationForm notificationForm = NotificationForm.newInstance(
                notificationService.notificationsPaginated(receiptUser.getRid(), current)
        );

        List<String> notifications = new LinkedList<>();
        for (NotificationDetailForm notificationDetailForm : notificationForm.getNotifications()) {
            String messageForDisplay = notificationDetailForm.getNotificationMessageForDisplay();
            LOG.debug(messageForDisplay);
            notifications.add(
                    notificationDetailForm.getHref() +
                            ":" +
                            notificationDetailForm.getAbbreviatedMessage() +
                            ":" +
                            notificationDetailForm.getCreatedStr()
            );
        }

        return notifications;
    }
}
