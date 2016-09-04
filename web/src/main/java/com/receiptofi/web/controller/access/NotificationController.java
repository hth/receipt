package com.receiptofi.web.controller.access;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.NotificationService;
import com.receiptofi.web.form.NotificationDetailForm;
import com.receiptofi.web.form.NotificationForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 7/1/13
 * Time: 9:51 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access")
public class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    private final NotificationService notificationService;

    /**
     * maps to notification.jsp
     */
    @Value ("${next.page:/notification}")
    private String nextPage;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/notification",
            method = RequestMethod.GET
    )
    public String loadForm(
            @ModelAttribute ("notificationForm")
            NotificationForm notificationForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("LandingController loadForm: " + receiptUser.getRid());

        List<NotificationEntity> notifications = notificationService.getAllNotifications(receiptUser.getRid());
        notificationForm.setNotifications(notifications);
        notificationForm.setCount(Integer.toString(notifications.size()));
        return nextPage;
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
            int current,

            @ModelAttribute ("notificationForm")
            NotificationForm notificationForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificationForm.setNotifications(notificationService.notificationsPaginated(receiptUser.getRid(), current));
        List<String> notifications = new LinkedList<>();
        for (NotificationDetailForm notificationDetailForm : notificationForm.getNotifications()) {
            notificationDetailForm.getNotificationMessageForDisplay();
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
