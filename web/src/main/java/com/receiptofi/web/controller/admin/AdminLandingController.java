/**
 *
 */
package com.receiptofi.web.controller.admin;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.web.form.SearchUserForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Redirect to prevent re-submit.
 *
 * @author hitender
 * @since Mar 26, 2013 1:14:24 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/admin")
public class AdminLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminLandingController.class);

    @Value ("${nextPage:/admin/landing}")
    private String nextPage;

    /**
     * Gymnastic for PRG example.
     *
     * @param searchUserForm
     * @return
     */
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("userSearchForm")
            SearchUserForm searchUserForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on admin page rid={}", receiptUser.getRid());
        return nextPage;
    }
}
