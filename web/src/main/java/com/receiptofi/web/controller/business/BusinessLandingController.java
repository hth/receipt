package com.receiptofi.web.controller.business;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.web.form.UserSearchForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @Value ("${nextPage:/business/landing}")
    private String nextPage;

    /**
     * Gymnastic for PRG example.
     *
     * @param userSearchForm
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_BUSINESS')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("userSearchForm")
            UserSearchForm userSearchForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
        return nextPage;
    }
}
