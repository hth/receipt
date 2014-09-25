package com.receiptofi.web.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hitender
 * Date: 9/25/14 12:26 AM
 */
public final class HttpRequestResponseParser {

    public static String printHeader(HttpServletRequest httpServletRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = httpServletRequest.getHeader(headerName);
            stringBuilder.append(headerName).append(":").append(headerValue).append(",");
        }
        return stringBuilder.toString();
    }
}
