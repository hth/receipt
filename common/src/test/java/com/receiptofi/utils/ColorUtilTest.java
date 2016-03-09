package com.receiptofi.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * User: hitender
 * Date: 3/7/16 11:55 PM
 */
public class ColorUtilTest {

    @Test
    public void testGetRandomSize() throws Exception {
        assertEquals("#ABC123".length(), ColorUtil.getRandom().length());
    }

    @Test
    public void testGetRandomFirstChar() throws Exception {
        assertEquals('#', ColorUtil.getRandom().charAt(0));
    }
}
