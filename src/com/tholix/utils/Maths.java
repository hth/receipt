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

    public static BigDecimal add(BigDecimal a, String b) {
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

    /**
     *
     * @param from - Higher than value
     * @param value - Lower than from
     * @return
     */
    public static BigDecimal subtract(BigDecimal from, BigDecimal value) {
        BigDecimal sub = from.subtract(value);
        sub = sub.setScale(2, BigDecimal.ROUND_HALF_UP);
        log.debug("subtract: " + from + " - " + value + " = " + sub);
        return sub;
    }

    public static BigDecimal subtract(Double from, Double value) {
        return subtract(new BigDecimal(from.toString()), new BigDecimal(value.toString()));
    }

    /**
     *
     * @param divide - Value to be divided
     * @param by - Divide by this
     * @return
     */
    public static BigDecimal divide(BigDecimal divide, BigDecimal by) {
        BigDecimal division = divide.divide(by, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        division = division.setScale(2, BigDecimal.ROUND_HALF_UP);
        log.debug("divide: " + divide + " / " + by + " = " + division);
        return division;
    }

    public static BigDecimal divide(BigDecimal divide, Double by) {
        return divide(divide, new BigDecimal(by.toString()));
    }

    public static BigDecimal divide(BigDecimal divide, int by) {
        return divide(divide, new BigDecimal(by));
    }

    /**
     * Should be used in percentage calculation with default scale of 4 everywhere.
     * Can be made private for receipt entity.
     *
     * @param divide
     * @param by
     * @param scale
     * @return
     */
    public static BigDecimal divide(Double divide, Double by, int scale) {
        BigDecimal total = new BigDecimal(divide.toString());
        BigDecimal subTotal = new BigDecimal(by.toString());
        BigDecimal outcome = total.divide(subTotal, scale, BigDecimal.ROUND_HALF_UP);
        return outcome;
    }

    /**
     * Plain multiplication of two numbers
     *
     * @param value
     * @param withThis
     * @return
     */
    public static BigDecimal multiply(BigDecimal value, BigDecimal withThis) {
        BigDecimal multiplication = value.multiply(withThis);
        multiplication = multiplication.setScale(2, BigDecimal.ROUND_HALF_UP);
        log.debug("multiply: " + value + " * " + withThis + " = " + multiplication);
        return multiplication;
    }

    public static BigDecimal multiply(BigDecimal value, String withThis) {
        return multiply(value, new BigDecimal(withThis));
    }

    public static BigDecimal multiply(BigDecimal value, Double withThis) {
        return multiply(value, new BigDecimal(withThis.toString()));
    }

    public static BigDecimal percent(BigDecimal value) {
        return multiply(value, Maths.multiply(BigDecimal.TEN, BigDecimal.TEN));
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
}
