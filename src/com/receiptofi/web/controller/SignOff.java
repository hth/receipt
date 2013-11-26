package com.receiptofi.web.controller;

import com.receiptofi.domain.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * User: hitender
 * Date: 7/2/13
 * Time: 10:41 PM
 */
@Controller
@RequestMapping(value = "/signoff")
@SessionAttributes({"userSession"})
public class SignOff {
    private static final Logger log = LoggerFactory.getLogger(SignOff.class);

    private static final String SIGN_OFF = "/signoff";

    @RequestMapping(method = RequestMethod.GET)
    public String signoff(@ModelAttribute UserSession userSession, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        log.info(("logged out: " + userSession.getUserProfileId()));
        return SIGN_OFF;
    }
}
