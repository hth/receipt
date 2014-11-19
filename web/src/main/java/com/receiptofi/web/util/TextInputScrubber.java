package com.receiptofi.web.util;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.owasp.html.HtmlPolicyBuilder;

/**
 * User: hitender
 * Date: 11/24/13 11:28 AM
 */
public final class TextInputScrubber {
    private static final Logger LOG = LoggerFactory.getLogger(TextInputScrubber.class);

    private TextInputScrubber() {
    }

    public static String scrub(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        String decoded;
        try {
            decoded = URLDecoder.decode(text, "UTF-8");
        } catch (Exception exce) {
            LOG.warn("Decode failed text={}", text, exce);
            return StringUtils.EMPTY;
        }

        //Using Spring instead of StringEscapeUtils
        return new HtmlPolicyBuilder().toFactory().sanitize(decoded);
    }
}
