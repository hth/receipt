package com.receiptofi.web.controller.display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: hitender
 * Date: 11/18/14 10:29 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Controller
@RequestMapping (value = "/display")
public class DisplayLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayLandingController.class);

    /**
     * Refers to landing.jsp
     */
    @Value ("${nextPage:/display/landing}")
    private String nextPage;

    @PreAuthorize ("hasRole('ROLE_ANALYSIS_READ')")
    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET
    )
    public ModelAndView loadForm() {
        ModelAndView modelAndView = new ModelAndView(nextPage);
        return modelAndView;
    }
}
