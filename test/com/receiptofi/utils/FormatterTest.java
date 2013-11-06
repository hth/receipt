package com.receiptofi.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * User: hitender
 * Date: 4/18/13
 * Time: 10:11 PM
 */
public class FormatterTest {
    @Test
    public void testGetCurrencyFormatted() throws Exception {
        assertEquals(Double.valueOf(20.00), Formatter.getCurrencyFormatted("20"));
    }


}
