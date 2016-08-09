package com.receiptofi.web.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.context.ExternalContext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hitender
 * Date: 8/8/16 7:06 PM
 */
@Component
public class WebFlowUtils {

    public Object getFlashAttribute(ExternalContext context, String attributeName) {
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap((HttpServletRequest) context.getNativeRequest());
        return flashMap != null ? flashMap.get(attributeName) : null;
    }
}
