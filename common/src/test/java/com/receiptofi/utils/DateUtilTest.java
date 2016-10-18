package com.receiptofi.utils;

import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;

import java.time.LocalDateTime;

/**
 * User: hitender
 * Date: 10/12/15 7:03 PM
 */
public class DateUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtilTest.class);

    @Test
    public void caseSensitivityAMPM() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LOG.info(localDateTime.format(DateUtil.DateType.DT5.getFormatter()));

        assertEquals(
                DateUtil.getDateFromString("01/01/2016 4:00:00 pM"),
                DateUtil.getDateFromString("01/01/2016 16:00:00"));
    }

    @Test
    public void DT1_DT101() {
        assertEquals(
                DateUtil.getDateFromString("01/01/2016 04:00:00 pM"),
                DateUtil.getDateFromString("01/01/2016 4:00:00 pM"));
    }

    @Test
    public void DT102_DT2() {
        assertEquals(
                DateUtil.getDateFromString("01/01/2016 4:00:00"),
                DateUtil.getDateFromString("01/01/2016 04:00:00"));
    }

    @Test
    public void DT3_DT301() {
        assertEquals(
                DateUtil.getDateFromString("1/01/2016 01:00:00 AM"),
                DateUtil.getDateFromString("1/01/2016 1:00:00 AM"));
    }

    @Test
    public void DT302_DT4() {
        assertEquals(
                DateUtil.getDateFromString("1/01/2016 1:00:00"),
                DateUtil.getDateFromString("1/01/2016 01:00:00"));
    }

    @Test
    public void DT5_DT501() {
        assertEquals(
                DateUtil.getDateFromString("01/1/2016 01:00:00 AM"),
                DateUtil.getDateFromString("01/1/2016 1:00:00 AM"));
    }

    @Test
    public void DT502_DT6() {
        assertEquals(
                DateUtil.getDateFromString("01/1/2016 1:00:00"),
                DateUtil.getDateFromString("01/1/2016 01:00:00"));
    }

    @Test
    public void DT7_DT701() {
        assertEquals(
                DateUtil.getDateFromString("1/1/2016 01:00:00 AM"),
                DateUtil.getDateFromString("1/1/2016 1:00:00 AM"));
    }

    @Test
    public void DT702_DT8() {
        assertEquals(
                DateUtil.getDateFromString("1/1/2016 1:00:00"),
                DateUtil.getDateFromString("1/1/2016 01:00:00"));
    }

    @Test
    public void DT9_DT901() {
        assertEquals(
                DateUtil.getDateFromString("01/01/2016 01:00 AM"),
                DateUtil.getDateFromString("01/01/2016 1:00 AM"));
    }

    @Test
    public void DT902_DT10() {
        assertEquals(
                DateUtil.getDateFromString("01/01/2016 1:00"),
                DateUtil.getDateFromString("01/01/2016 01:00"));
    }

    @Test
    public void DT11_DT1101() {
        assertEquals(
                DateUtil.getDateFromString("1/01/2016 01:00 AM"),
                DateUtil.getDateFromString("1/01/2016 1:00 AM"));
    }

    @Test
    public void DT1102_DT12() {
        assertEquals(
                DateUtil.getDateFromString("1/01/2016 1:00"),
                DateUtil.getDateFromString("1/01/2016 01:00"));
    }

    @Test
    public void DT13_DT1301() {
        assertEquals(
                DateUtil.getDateFromString("01/1/2016 01:00 AM"),
                DateUtil.getDateFromString("01/1/2016 1:00 AM"));
    }

    @Test
    public void DT1302_DT14() {
        assertEquals(
                DateUtil.getDateFromString("01/1/2016 1:00"),
                DateUtil.getDateFromString("01/1/2016 01:00"));
    }

    @Test
    public void DT15_DT1501() {
        assertEquals(
                DateUtil.getDateFromString("1/1/2016 01:00 AM"),
                DateUtil.getDateFromString("1/1/2016 1:00 AM"));
    }

    @Test
    public void DT1502_DT16() {
        assertEquals(
                DateUtil.getDateFromString("1/1/2016 1:00"),
                DateUtil.getDateFromString("1/1/2016 01:00"));
    }

    @Test
    public void DT17() {
        assertEquals(
                DateUtil.getDateFromString("1/1/2016"),
                DateUtil.getDateFromString("01/01/2016"));
    }
}
