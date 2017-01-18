package com.receiptofi.utils;

import org.springframework.util.CollectionUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hitender
 * Date: 1/18/17 1:52 PM
 */
public final class HttpUtil {

    /* https://stackoverflow.com/questions/24894093/ruby-regular-expression-extracting-part-of-url */
    static final Pattern EXTRACT_ENDPOINT_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");

    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    public static String extractDataFromURL(String uri, String group) {
        return EXTRACT_ENDPOINT_PATTERN.matcher(uri).replaceFirst(group);
    }
    
    public static String extractDataFromURL(String uri, String group, String skipName) {
        return extractDataFromURL(uri, group).replaceFirst(skipName, "");
    }

    public static String getHeader(Map<String, String> allHeadersMap, String header) {
        return CollectionUtils.isEmpty(allHeadersMap) && !allHeadersMap.containsKey(header) ? "" : allHeadersMap.get(header);
    }
}
