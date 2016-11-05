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

    private static Map<String, Map<String, Locale>> locales;

    /**
     * Gets first available locale for specified country.
     *
     * @param countryCode
     * @return
     */
    static Locale getCountrySpecificLocale(String countryCode) {
        //TODO uncomment the log below
        //LOG.debug("Country code={}", countryCode);
        Map<String, Locale> supportedLocale;
        if (locales == null) {
            locales = new HashMap<>();
            for (Locale locale : Locale.getAvailableLocales()) {

                if (locales.containsKey(locale.getCountry())) {
                    supportedLocale = locales.get(locale.getCountry());
                    supportedLocale.put(locale.getLanguage(), locale);
                } else {
                    supportedLocale = new HashMap<>();
                    supportedLocale.put(locale.getLanguage(), locale);
                }
                locales.put(locale.getCountry(), supportedLocale);
            }
        }

        if (StringUtils.isNotEmpty(countryCode)) {
            supportedLocale = locales.get(countryCode);
            if (supportedLocale != null) {
                if (supportedLocale.containsKey(Locale.US.getLanguage())) {
                    return supportedLocale.get(Locale.US.getLanguage());
                } else {
                    return supportedLocale.get(supportedLocale.keySet().iterator().next());
                }
            }
        }

        LOG.warn("Returning default locale US as locale='{}' not found", countryCode);
        return Locale.US;
    }

    /**
     * Number format for country code.
     *
     * @param countryCode
     * @return
     */
    public static NumberFormat getNumberFormat(String countryCode) {
        return NumberFormat.getCurrencyInstance(getCountrySpecificLocale(countryCode));
    }
}
