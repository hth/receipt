package com.receiptofi.web.controller.accountant;

import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.AccountantService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.web.form.accountant.AccountantLandingForm;
import com.receiptofi.web.form.business.BusinessLandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private BusinessUserService businessUserService;

    @Autowired
    public AccountantLandingController(
            @Value ("${accountantLanding:/accountant/landing}")
            String accountantLanding,

            AccountantService accountantService,
            BusinessUserService businessUserService) {
        this.accountantLanding = accountantLanding;
        this.accountantService = accountantService;
        this.businessUserService = businessUserService;
    }

    @RequestMapping (method = RequestMethod.GET, value = "/landing")
    public String loadForm(@ModelAttribute("accountantLandingForm") AccountantLandingForm accountantLandingForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business expenses tally page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());


        BusinessUserEntity businessUser = businessUserService.findBusinessUser(receiptUser.getRid());
        return nextPage(receiptUser, businessUser, accountantLandingForm);
    }

    private String nextPage(
            ReceiptUser receiptUser,
            BusinessUserEntity businessUser,
            AccountantLandingForm accountantLandingForm) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                accountantLandingForm.setAccountants(accountantService.getUsersSubscribedToAccountant(receiptUser.getRid()));
                return accountantLanding;
            case C:
            case I:
            case N:
                //TODO show message saying account is still being validated
                LOG.info("Migrate to business registration rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
                return accountantLanding;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }
}
