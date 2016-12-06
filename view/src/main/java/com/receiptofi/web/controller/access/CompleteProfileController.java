package com.receiptofi.web.controller.access;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.web.controller.open.LoginController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: hitender
 * Date: 5/10/14 10:31 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/completeprofile")
public class CompleteProfileController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Value ("${maxSkipProfileUpdate:5}")
    private int maxSkipProfileUpdate;

    /**
     * Loads initial form.
     *
     * @return
     */
    @RequestMapping (method = RequestMethod.GET)
    public String completeProfile() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "completeprofile";
    }

    //XXX TODO complete this to update profile; can skip max of 5 times should be configurable
    @RequestMapping (method = RequestMethod.POST)
    public String updateProfile() {
        return "redirect:/access/landing.htm";
    }
}
