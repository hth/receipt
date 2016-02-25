package com.receiptofi.utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 2/12/16 11:13 AM
 */
public class LocaleUtil {
    private static Map<String, Locale> locales;

    /**
     * Gets first available locale for specified country.
     *
     * @param countryCode
     * @return
     */
    public static Locale getCountrySpecificLocale(String countryCode) {
        if (locales == null) {
            locales = new HashMap<>();
            for (Locale locale : Locale.getAvailableLocales()) {
                locales.put(locale.getCountry(), locale);
            }
        }

        return locales.get(countryCode);
    }

    /**
     * Currency symbol like $ or INR.
     *
     * @param countryCode
     * @return
     */
    @SuppressWarnings("unused")
    public static String getCurrencySymbol(String countryCode) {
        return getNumberFormat(countryCode).getCurrency().getSymbol();
    }

    /**
     * Number format for country code
     *
     * @param countryCode
     * @return
     */
    public static NumberFormat getNumberFormat(String countryCode) {
        return NumberFormat.getCurrencyInstance(getCountrySpecificLocale(countryCode));
    }
}
