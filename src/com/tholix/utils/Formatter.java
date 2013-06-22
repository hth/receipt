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

	public static BigDecimal getCurrencyFormatted(String value) throws ParseException, NumberFormatException {
		BigDecimal d;
		if(value.startsWith("$")) {
			Number number = defaultFormat.parse(value);
			d = new BigDecimal(number.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			d = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		return d;
	}
}
