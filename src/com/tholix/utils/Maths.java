package com.tholix.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * User: hitender
 * Date: 4/9/13
 * Time: 9:42 AM
 * {@link http://java-performance.info/bigdecimal-vs-double-in-financial-calculations/}
 */
public final class Maths {
    private static volatile Logger log = Logger.getLogger(Maths.class);

    //double[] values = { 1.0, 3.5, 123.4567, 10.0 };
    //output 1 3.5 123.457 10
    private volatile DecimalFormat df = new DecimalFormat("0.###");

    public static int add(int a, int b) {
        return a + b;
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        log.debug("addition: " + a + " + " + b + " = " + a.add(b));
        return a.add(b);
    }

    public static BigDecimal add(String a, String b) {
        if(StringUtils.isNotEmpty(b) && StringUtils.isNotEmpty(a) && isNumeric(a) && isNumeric(b)) {
            BigDecimal x = new BigDecimal(a);
            BigDecimal y = new BigDecimal(b);
            return add(x, y);
        }
        throw new ArithmeticException("Value is not a number: ' " + a + " ', ' " + b + " '");
    }

    private static BigDecimal add(BigDecimal a, String b) {
        return add(a, new BigDecimal(b));
    }

    public static BigDecimal add(BigDecimal a, double b) {
        try {
            if(Double.isNaN(b)) {
                throw new ArithmeticException("Value is not a number: ' " + b + " '");
            }
            return add(a, Double.toString(b));
        } catch(Exception exce) {
            throw new ArithmeticException(exce.getLocalizedMessage());
        }
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
}
