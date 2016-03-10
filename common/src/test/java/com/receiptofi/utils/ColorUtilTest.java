package com.receiptofi.utils;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * User: hitender
 * Date: 3/7/16 11:55 PM
 */
public class ColorUtilTest {

    @Test
    public void testGetRandomSize() throws Exception {
        assertThat(ColorUtil.getRandom().length(), lessThanOrEqualTo(7));
    }

    @Test
    public void testGetRandomFirstChar() throws Exception {
        assertEquals('#', ColorUtil.getRandom().charAt(0));
    }
}
