package com.receiptofi.utils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * User: hitender
 * Date: 11/24/13 11:28 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class TextInputScrubber {
    private static final Logger LOG = LoggerFactory.getLogger(TextInputScrubber.class);

    private TextInputScrubber() {
    }

    /**
     * Takes the input and removes all script and html tags it finds, returning what is left.
     * This also converts any HTML character entities to their UTF-8 character equivalents.
     *
     * @param input The input text to scrub
     * @return the scrubbed text or just a blank string if nothing is left
     */
    public static String sanitize(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        PolicyFactory policyFactory = new HtmlPolicyBuilder().toFactory();
        int preSanitizeLength;
        String sanitizedText = input;
        while (true) {
            preSanitizeLength = sanitizedText.length();
            sanitizedText = StringEscapeUtils.unescapeHtml(policyFactory.sanitize(sanitizedText));

            if (sanitizedText.length() > preSanitizeLength) {
                LOG.warn("input grew: [{}]", input);
                return "";
            } else if (preSanitizeLength == sanitizedText.length()) {
                return sanitizedText;
            }
        }
    }

    public static String decode(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable to decode the input={}, next trying replacing text.", input);
            input = input.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            input = input.replaceAll("\\+", "%2B");
            try {
                return URLDecoder.decode(input, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                LOG.error("Unable to decode the input={}", input, e);
                return "";
            }
        } catch (Exception e) {
            LOG.error("Unable to decode the input={}", input, e);
            return "";
        }
    }
}
