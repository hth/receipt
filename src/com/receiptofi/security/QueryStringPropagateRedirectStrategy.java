package com.receiptofi.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User: hitender
 * Date: 5/29/14 6:24 PM
 */
public class QueryStringPropagateRedirectStrategy extends DefaultRedirectStrategy {
    private static final String errorParameter   = "?error=--#";

    /**
     * On login failure, redirects user to login form with appropriate error message or returns json
     * @param request
     * @param response
     * @param url
     * @throws IOException
     */
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if(request.getHeader("cookie") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); //re-direct does not work
        } else {
            super.sendRedirect(request, response, url + errorParameter);
        }
    }
}
