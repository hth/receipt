package com.receiptofi.web.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import org.springframework.web.util.HtmlUtils;

/**
 * User: hitender
 * Date: 11/24/13 11:28 AM
 */
public final class TextInputScrubber {
    private static Logger log = LoggerFactory.getLogger(TextInputScrubber.class);

    private static final PolicyFactory factory = new HtmlPolicyBuilder().toFactory();

    public static String scrub(String text) {
        if(StringUtils.isBlank(text)) {
            return text;
        }

        String decoded;
        try {
            decoded = URLDecoder.decode(text, "UTF-8");
        } catch(Exception exce) {
            log.error("Text decode failed: " + text);
            return StringUtils.EMPTY;
        }

        String scrubbedText = factory.sanitize(decoded);

        //Using Spring instead of StringEscapeUtils
        return HtmlUtils.htmlUnescape(scrubbedText);
    }
}
