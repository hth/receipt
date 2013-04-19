package com.tholix.utils;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: hitender
 * Date: 4/18/13
 * Time: 9:39 PM
 */
public class SHAHashingTest {

    @Test
    public void testHashCodeSHA1() throws Exception {
        assertEquals("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d",
                SHAHashing.hashCodeSHA1("hello"));
    }

    @Test
    public void testHashCodeSHA512() throws Exception {
        assertEquals("9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043",
                SHAHashing.hashCodeSHA512("hello"));
    }
}
