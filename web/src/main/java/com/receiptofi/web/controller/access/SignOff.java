package com.receiptofi.web.controller.access;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.receiptofi.domain.site.ReceiptUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Custom logout override spring logout.
 * User: hitender
 * Date: 7/2/13
 * Time: 10:41 PM
 */
@Controller
@RequestMapping (value = "/access/signoff")
public final class SignOff extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SignOff.class);

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String receiptUserId = "Not Available";
        if (authentication.getPrincipal() != null) {
            receiptUserId = ((ReceiptUser) authentication.getPrincipal()).getRid();
        }

        LOG.info("Logout user={} from={}", receiptUserId, request.getServletPath());
        super.onLogoutSuccess(request, response, authentication);
    }
}
