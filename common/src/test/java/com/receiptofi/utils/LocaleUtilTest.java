package com.receiptofi.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * User: hitender
 * Date: 3/7/16 4:49 PM
 */
public class LocaleUtilTest {

    @Test
    public void testGetCountrySpecificLocale() throws Exception {
        Locale locale = LocaleUtil.getCountrySpecificLocale("US");
        assertEquals(Locale.US, locale);

        locale = LocaleUtil.getCountrySpecificLocale("SOME_COUNTRY");
        assertEquals(Locale.US, locale);
    }

    @Test
    public void testGetNumberFormat() throws Exception {
        NumberFormat numberFormat = LocaleUtil.getNumberFormat("IN");
        assertEquals("Rs.10.00", numberFormat.format(10.00));

        numberFormat = LocaleUtil.getNumberFormat(Locale.KOREA.getCountry());
        assertEquals("￦10", numberFormat.format(10.00));

        numberFormat = LocaleUtil.getNumberFormat("FR");
        assertEquals("10,00 €", numberFormat.format(10.00));

        numberFormat = LocaleUtil.getNumberFormat(null);
        assertEquals("$10.00", numberFormat.format(10.00));
    }
}