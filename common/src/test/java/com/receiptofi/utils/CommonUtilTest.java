package com.receiptofi.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * User: hitender
 * Date: 3/8/16 12:01 AM
 */
public class CommonUtilTest {

    @Test
    public void testIsJSONValidEmpty() throws Exception {
        assertTrue(CommonUtil.isJSONValid("{}"));
        assertTrue(CommonUtil.isJSONValid("{}"));
    }

    @Test
    public void testIsJSONValid() throws Exception {
        assertTrue(CommonUtil.isJSONValid("{'name' : 'First'}"));
    }
    @Test
    public void testIsJSONNotValid() throws Exception {
        assertFalse(CommonUtil.isJSONValid("{'name : 'First'}"));
    }

}
