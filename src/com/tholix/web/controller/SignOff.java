package com.tholix.web.controller;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.tholix.domain.UserSession;

/**
 * User: hitender
 * Date: 7/2/13
 * Time: 10:41 PM
 */
@Controller
@RequestMapping(value = "/signoff")
@SessionAttributes({"userSession"})
public class SignOff {
    private static final Logger log = Logger.getLogger(SignOff.class);

    private static final String SIGN_OFF = "/signoff";

    @RequestMapping(value = "/signoff", method = RequestMethod.GET)
    public String signoff(@ModelAttribute UserSession userSession, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        log.info(("logged out: " + userSession.getUserProfileId()));
        return SIGN_OFF;
    }
}
