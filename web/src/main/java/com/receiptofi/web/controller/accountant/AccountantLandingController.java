package com.receiptofi.web.controller.accountant;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.AccountantService;

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
 * Date: 7/23/16 9:26 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/accountant")
public class AccountantLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountantLandingController.class);

    private String accountantLanding;
    private AccountantService accountantService;

    @Autowired
    public AccountantLandingController(
            @Value ("${accountantLanding:/accountant/landing}")
            String accountantLanding,

            AccountantService accountantService) {
        this.accountantLanding = accountantLanding;
        this.accountantService = accountantService;
    }

    @RequestMapping (method = RequestMethod.GET)
    public ModelAndView loadForm() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business expenses tally page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());

        ModelAndView modelAndView = new ModelAndView(accountantLanding);
        modelAndView.addObject("accounts", accountantService.getUsersSubscribedToAccountant(receiptUser.getRid()));
        return modelAndView;
    }
}
