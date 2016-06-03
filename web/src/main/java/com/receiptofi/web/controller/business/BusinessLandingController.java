package com.receiptofi.web.controller.business;

import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.BusinessUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * For Businesses.
 * User: hitender
 * Date: 5/13/16 1:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business")
public class BusinessLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLandingController.class);

    private String nextPage;
    private String businessRegistrationFlow;
    private BusinessUserService businessUserService;

    @Autowired
    public BusinessLandingController(
            @Value ("${nextPage:/business/landing}")
            String nextPage,

            @Value ("${businessRegistrationFlow:redirect:/business/registration.htm}")
            String businessRegistrationFlow,

            BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.businessRegistrationFlow = businessRegistrationFlow;
        this.businessUserService = businessUserService;
    }

    /**
     * Gymnastic for PRG example.
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_BUSINESS')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(receiptUser.getRid());
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                return nextPage;
            case C:
            case I:
            case N:
                LOG.info("Business Registration rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
                return businessRegistrationFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }
}
