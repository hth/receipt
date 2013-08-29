/**
 *
 */
package com.tholix.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;

/**
 * @author hitender
 * @since Jan 9, 2013 11:13:30 PM
 *
 */
public final class Formatter {
	private static final Logger log = Logger.getLogger(Formatter.class);

	/** For double */
	public static DecimalFormat df = new DecimalFormat("#.##");

	public static NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

    //Refer bug #3
    //TODO may be change this method to support just item format and net format. Means have two method with scale of 2 and 4. 2 scale for total; and 4 scale for
	public static BigDecimal getCurrencyFormatted(String value) throws ParseException {
		BigDecimal d;
        try {
            if(value.startsWith("$")) {
                Number number = defaultFormat.parse(value);
                d = new BigDecimal(number.doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP);
            } else {
                d = new BigDecimal(value).setScale(4, BigDecimal.ROUND_HALF_UP);
            }

            return d;
        } catch(NumberFormatException nfe) {
            log.error("Error parsing number value: " + value + ", exception: " + nfe);
            throw new NumberFormatException("Error parsing number value: " + value + ", exception: " + nfe);
        }
	}
}
