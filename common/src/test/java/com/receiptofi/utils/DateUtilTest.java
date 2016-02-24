package com.receiptofi.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * User: hitender
 * Date: 10/12/15 7:03 PM
 */
public class DateUtilTest {

    @Test
    public void testGetDateFromString() throws Exception {
        assertEquals(
                DateUtil.getDateFromString("10/10/2015 4:00:00 PM"),
                DateUtil.getDateFromString("10/10/2015 16:00:00"));

        assertEquals(
                DateUtil.getDateFromString("10/10/2015 4:00:00 pm"),
                DateUtil.getDateFromString("10/10/2015 16:00:00"));

        assertEquals(
                DateUtil.getDateFromString("10/10/15 4:00:00 pm"),
                DateUtil.getDateFromString("10/10/15 16:00:00"));
    }
}
