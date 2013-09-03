/**
 *
 */
package com.tholix.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * @author hitender
 * @since Jan 9, 2013 11:13:30 PM
 *
 */
public final class Formatter {
	private static final Logger log = Logger.getLogger(Formatter.class);

    //Defaults to US
    private static String FORMAT_TO_US = "US";

	/** For double */
	public static DecimalFormat df = new DecimalFormat("#.##");

	public static NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
    private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

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

    /**
     * Helps format phone numbers
     *
     * @param phone
     * @return
     */
    public static String phone(String phone) {
        try {
            //Currently defaults to US
            Phonenumber.PhoneNumber numberPrototype = phoneUtil.parse(phone, FORMAT_TO_US);
            return phoneUtil.format(numberPrototype, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            log.error("NumberParseException was thrown while parsing the phone number : " + e.toString());
            return "";
        }
    }
}
