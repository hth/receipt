/**
 *
 */
package com.tholix.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;

/**
 * @author hitender
 * @when Jan 9, 2013 11:13:30 PM
 *
 */
public final class Formatter {
	private static final Logger log = Logger.getLogger(Formatter.class);

	/** For double */
	public static DecimalFormat df = new DecimalFormat("#.##");

	public static NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

	public static Double getCurrencyFormatted(String value) throws ParseException {
		Double d;
		if(value.startsWith("$")) {
			Number number = defaultFormat.parse(value);
			d = number.doubleValue();
		} else {
			d = Double.valueOf(value);
		}

		return d;
	}
}
