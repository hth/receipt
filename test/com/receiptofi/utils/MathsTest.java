package com.receiptofi.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * User: hitender
 * Date: 4/9/13
 * Time: 8:00 PM
 */
public class MathsTest {
    @Test
    public void testAdd() throws Exception {
        assertEquals(30, Maths.add(10, 20));
        assertEquals(new BigDecimal("31.00"),   Maths.add(new BigDecimal("10.50"), new BigDecimal("20.50")));
        assertEquals(new BigDecimal("94.94"),   Maths.add("42.42", "52.52"));
        assertEquals(new BigDecimal("80.55"),   Maths.add(new BigDecimal("60.50"), 20.05d));
    }

    @Test(expected = ArithmeticException.class)
    public void testArithmeticException() {
        Maths.add("b", "b");
    }

    @Test
    public void testIsNumeric() throws Exception {
        assertEquals(false, Maths.isNumeric("abc"));
        assertEquals(true, Maths.isNumeric("10.0"));
        assertEquals(true, Maths.isNumeric("10"));
        assertEquals(true, Maths.isNumeric("0.10"));
    }
}
