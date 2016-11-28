package com.receiptofi.web.controller.admin;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.NotificationService;
import com.receiptofi.web.form.admin.NotificationSendForm;
import com.receiptofi.web.validator.admin.NotificationValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User: hitender
 * Date: 11/27/16 5:22 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/admin")
public class NotificationSendController {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSendController.class);

    @Value ("${nextPage:/admin/notificationSend}")
    private String nextPage;

    private NotificationValidator notificationValidator;
    private NotificationService notificationService;
    private AccountService accountService;

    @Autowired
    public NotificationSendController(
            NotificationValidator notificationValidator,
            NotificationService notificationService,
            AccountService accountService) {
        this.notificationValidator = notificationValidator;
        this.notificationService = notificationService;
        this.accountService = accountService;
    }

    /**
     * Gymnastic for PRG example.
     *
     * @param notificationSendForm
     * @return
     */
    @RequestMapping (value = "/notificationSend", method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("notificationSendForm")
            NotificationSendForm notificationSendForm,

            Model model,
            RedirectAttributes redirectAttrs
    ) {
        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.notificationSendForm", model.asMap().get("result"));
        } else {
            redirectAttrs.addFlashAttribute("notificationSendForm", notificationSendForm);
        }
        return nextPage;
    }

    /**
     * Gymnastic for PRG example.
     *
     * @param notificationSendForm
     * @return
     */
    @RequestMapping (value = "/notificationSend", method = RequestMethod.POST)
    public String loadUser(
            @ModelAttribute ("notificationSendForm")
            NotificationSendForm notificationSendForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        String rid = notificationSendForm.getRid().getText();
        LOG.info("Send notification rid={} message={}",
                rid,
                notificationSendForm.getMessage().getText());

        notificationValidator.validate(notificationSendForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            //Re-direct to prevent resubmit
            return "redirect:" + nextPage + ".htm";
        }

        try {
            UserAccountEntity userAccount = accountService.findByReceiptUserId(rid);
            if (null != userAccount) {
                notificationService.addNotification(
                        notificationSendForm.getMessage().getText(),
                        NotificationTypeEnum.MESSAGE,
                        NotificationGroupEnum.N,
                        notificationSendForm.getRid().getText());

                notificationSendForm.setSuccessMessage("Send message successfully to " + notificationSendForm.getRid());
                redirectAttrs.addFlashAttribute("notificationSendForm", notificationSendForm);
                /** Re-direct to prevent resubmit. */
                return "redirect:" + nextPage + ".htm";
            } else {
                notificationSendForm.setErrorMessage("RID: " + rid + ", not found");
                redirectAttrs.addFlashAttribute("notificationSendForm", notificationSendForm);
                return "redirect:" + nextPage + ".htm";
            }
        } catch (Exception e) {
            LOG.error("Failed to send notification reason={}", e.getLocalizedMessage(), e);
            notificationSendForm.setErrorMessage("Failed to send notification: " + e.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("notificationSendForm", notificationSendForm);
            return "redirect:" + nextPage + ".htm";
        }
    }
}
