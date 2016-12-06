package com.receiptofi.web.controller.access;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.MailService;
import com.receiptofi.utils.ScrubbedInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: hitender
 * Date: 8/10/16 3:44 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/invite")
public class InviteInternalController {
    private static final Logger LOG = LoggerFactory.getLogger(ExpensesController.class);

    private MailService mailService;

    @Autowired
    public InviteInternalController(MailService mailService) {
        this.mailService = mailService;
    }

    @RequestMapping (value = "/accountant", method = RequestMethod.POST)
    @ResponseBody
    public String inviteAccountant(
            @RequestParam (value = "mail")
                    ScrubbedInput mail
    ) {
        //Always lower case the email address
        String invitedUserEmail = mail.getText().toLowerCase();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOG.info("Invitation being sent to mail={} rid={}", invitedUserEmail, receiptUser.getRid());
        return mailService.sendBusinessInvite(
                invitedUserEmail,
                receiptUser.getRid(),
                receiptUser.getUsername(),
                UserLevelEnum.ACCOUNTANT);
    }

    @RequestMapping (value = "/business", method = RequestMethod.POST)
    @ResponseBody
    public String inviteBusiness(
            @RequestParam (value = "mail")
            ScrubbedInput mail
    ) {
        //Always lower case the email address
        String invitedUserEmail = mail.getText().toLowerCase();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOG.info("Invitation being sent to mail={} rid={}", invitedUserEmail, receiptUser.getRid());
        return mailService.sendBusinessInvite(
                invitedUserEmail,
                receiptUser.getRid(),
                receiptUser.getUsername(),
                UserLevelEnum.BUSINESS);
    }
}
