package com.receiptofi.utils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 2/12/16 11:13 AM
 */
public class LocaleUtil {
    private static final Logger LOG = LoggerFactory.getLogger(LocaleUtil.class);

    private static Map<String, Locale> locales;

    /**
     * Gets first available locale for specified country.
     *
     * @param countryCode
     * @return
     */
    public static Locale getCountrySpecificLocale(String countryCode) {
        LOG.debug("Country code={}", countryCode);
        if (locales == null) {
            locales = new HashMap<>();
            for (Locale locale : Locale.getAvailableLocales()) {
                locales.put(locale.getCountry(), locale);
            }
        }

        return locales.get(countryCode);
    }

    /**
     * Number format for country code.
     *
     * @param countryCode
     * @return
     */
    public static NumberFormat getNumberFormat(String countryCode) {
        if (StringUtils.isNotBlank(countryCode)) {
            return NumberFormat.getCurrencyInstance(getCountrySpecificLocale(countryCode));
        } else {
            LOG.info("Blank country code. Setting to US as default");
            return NumberFormat.getCurrencyInstance(getCountrySpecificLocale(Locale.US.getCountry()));
        }
    }
}
