package com.receiptofi.utils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * User: hitender
 * Date: 4/9/13
 * Time: 9:42 AM
 * {@see http://java-performance.info/bigdecimal-vs-double-in-financial-calculations/}
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class Maths {
    private static final Logger LOG = LoggerFactory.getLogger(Maths.class);

    /** Scale for Display is always two. */
    public static final int SCALE_TWO = 2;
    /** Minimum scale has to be four. Formatted to two decimal place for view but save data with four decimal places. */
    static final int SCALE_FOUR = 4;

    private static final int SCALE_SIX = 6;
    /** Accepted range in lowest denomination in cents here or any other currency. */
    public static final double ACCEPTED_RANGE_IN_LOWEST_DENOMINATION = 0.01;

    //double[] values = { 1.0, 3.5, 123.4567, 10.0 };
    //output 1 3.5 123.457 10
    private static final DecimalFormat DF = new DecimalFormat("0.###");

    private Maths() {
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        //LOG.debug("addition: " + a + " + " + b + " = " + a.add(b));
        return a.add(b);
    }

    public static BigDecimal add(String a, String b) {
        if (StringUtils.isNotEmpty(b) && StringUtils.isNotEmpty(a) && isNumeric(a) && isNumeric(b)) {
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
            if (Double.isNaN(b)) {
                throw new ArithmeticException("Value is not a number: ' " + b + " '");
            }
            return add(a, Double.toString(b));
        } catch (Exception exce) {
            throw new ArithmeticException(exce.getLocalizedMessage());
        }
    }

    /**
     * @param from  - Higher than value
     * @param value - Lower than from
     * @return
     */
    public static BigDecimal subtract(BigDecimal from, BigDecimal value) {
        BigDecimal sub = from.subtract(value);
        //This was messing the tax percentage calculation by rounding to 2. Why round subtraction?
        //sub = sub.setScale(2, BigDecimal.ROUND_HALF_UP);
        //LOG.debug("subtract: " + from + " - " + value + " = " + sub);
        return sub;
    }

    public static BigDecimal subtract(Double from, Double value) {
        return subtract(new BigDecimal(from.toString()), new BigDecimal(value.toString()));
    }

    /**
     * @param divide - Value to be divided
     * @param by     - Divide by this
     * @return
     */
    public static BigDecimal divide(BigDecimal divide, BigDecimal by) {
        try {
            BigDecimal division = divide.divide(by, SCALE_SIX, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
            division = division.setScale(SCALE_SIX, BigDecimal.ROUND_HALF_UP);
            return division;
        } catch (ArithmeticException exception) {
            // This should never occur. If this occur the problem is likely to be in code than receipt data.
            LOG.error("Tried Divide: {}, by: {}. Reason={}", divide, by, exception.getLocalizedMessage(), exception);
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal divide(long  divide, BigDecimal by) {
        return divide(new BigDecimal(divide), by);
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
     * @return
     */
    public static BigDecimal divide(Double divide, BigDecimal by) {
        BigDecimal total = new BigDecimal(divide.toString());
        return divide(total, by);
    }

    public static BigDecimal divide(Double divide, int by) {
        Assert.isTrue(by > 0, "Divide by should be greater than=" + by);
        BigDecimal total = new BigDecimal(divide.toString());
        return divide(total, by).setScale(2, 2);
    }

    /**
     * Plain multiplication of two numbers.
     *
     * @param value
     * @param withThis
     * @return
     */
    //TODO may be include strip trailing zeros
    public static BigDecimal multiply(BigDecimal value, BigDecimal withThis) {
        BigDecimal multiplication = value.multiply(withThis);
        multiplication = multiplication.setScale(SCALE_FOUR, BigDecimal.ROUND_HALF_UP);
        //LOG.debug("multiply: " + value + " * " + withThis + " = " + multiplication);
        return multiplication;
    }

    public static BigDecimal multiply(BigDecimal value, String withThis) {
        return multiply(value, new BigDecimal(withThis));
    }

    public static BigDecimal multiply(BigDecimal value, Double withThis) {
        return multiply(value, new BigDecimal(withThis.toString()));
    }

    public static BigDecimal multiply(Double value, int withThis) {
        return multiply(new BigDecimal(value.toString()), new BigDecimal(withThis));
    }

    public static BigDecimal multiply(Double value, Double withThis) {
        return multiply(new BigDecimal(value.toString()), new BigDecimal(withThis.toString()));
    }

    public static BigDecimal multiply(String value, String withThis) {
        return multiply(new BigDecimal(value), new BigDecimal(withThis));
    }

    public static BigDecimal multiply(BigDecimal value, int withThis) {
        return multiply(value, new BigDecimal(withThis));
    }

    public static BigDecimal percent(BigDecimal value) {
        return multiply(value, Maths.multiply(BigDecimal.TEN, BigDecimal.TEN));
    }

    private static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    /**
     * @param taxedAmount
     * @param withoutTaxedAmount
     * @return
     */
    public static BigDecimal calculateTax(Double taxedAmount, BigDecimal withoutTaxedAmount) {
        if (taxedAmount == 0 || withoutTaxedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return divide(taxedAmount, withoutTaxedAmount);
    }

    /**
     * Adjust scale to two decimal place and round half up.
     *
     * @param thisNumber
     * @return
     */
    public static BigDecimal adjustScale(BigDecimal thisNumber) {
        return thisNumber.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Adjust scale to two decimal place and round half up.
     *
     * @param thisNumber
     * @return
     */
    public static BigDecimal adjustScale(Double thisNumber) {
        return adjustScale(new BigDecimal(thisNumber, MathContext.DECIMAL64));
    }

    /**
     * This method is normally used for calculating something within acceptable range specified and accepted by system.
     *
     * @param from
     * @param value
     * @return
     */
    public static boolean withInRange(BigDecimal from, BigDecimal value) {
        Double diff = Math.abs(from.doubleValue() - value.doubleValue());
        //MathContext of '1' results in two decimal places, and '2' would result in three decimal places
        BigDecimal range = new BigDecimal(diff, new MathContext(1));
        return range.doubleValue() <= ACCEPTED_RANGE_IN_LOWEST_DENOMINATION;
    }
}
