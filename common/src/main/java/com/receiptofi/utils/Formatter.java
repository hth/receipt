/**
 *
 */
package com.receiptofi.utils;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance;
import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/** Define all singleton here */
enum FormatterSingleton {
    INSTANCE;

    protected ScriptEngine engine() {
        return new ScriptEngineManager().getEngineByName("JavaScript");
    }

    protected PhoneNumberUtil phoneInstance() {
        return getInstance();
    }

    protected NumberFormat currencyInstance() {
        return NumberFormat.getCurrencyInstance();
    }
}

/**
 * @author hitender
 * @since Jan 9, 2013 11:13:30 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class Formatter {
    /** For double */
    public static final DecimalFormat DF = new DecimalFormat("#.##");
    public static final SimpleDateFormat SDF_SMALL = new SimpleDateFormat("MM-dd-yyyy");
    public static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);
    //Defaults to US
    private static final String FORMAT_TO_US = "US";

    private Formatter() {
    }

    //Refer bug #3
    //TODO(hth) may be change this method to support just item format and net format.
    //TODO(hth) Means have two method with scale of 2 and 4. 2 scale for total; and 4 scale for
    public static BigDecimal getCurrencyFormatted(String value) throws ParseException, NumberFormatException {
        BigDecimal d;
        try {
            if (value.startsWith("$")) {
                Number number = FormatterSingleton.INSTANCE.currencyInstance().parse(value);
                d = new BigDecimal(number.doubleValue()).setScale(Maths.SCALE_FOUR, BigDecimal.ROUND_HALF_UP);
            } else {
                try {
                    Object object = FormatterSingleton.INSTANCE.engine().eval(value);
                    d = new BigDecimal(object.toString()).setScale(Maths.SCALE_FOUR, BigDecimal.ROUND_HALF_UP);
                } catch (ScriptException se) {
                    LOG.warn("Failed parsing number value={} reason={}", value, se.getLocalizedMessage(), se);
                    throw new NumberFormatException("Failed parsing number value: " + value + ", exception: " + se.getLocalizedMessage());
                }
                //d = new BigDecimal(value).setScale(Maths.SCALE_FOUR, BigDecimal.ROUND_HALF_UP);
            }
            return d;
        } catch (NumberFormatException nfe) {
            LOG.warn("Failed parsing number value={} reason={}", value, nfe.getLocalizedMessage(), nfe);
            throw new NumberFormatException("Failed parsing number value: " + value + ", exception: " + nfe);
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
            //TODO set using locale
            if (StringUtils.isBlank(phone)) {
                LOG.debug("phone number blank");
                return "";
            }
            PhoneNumber phoneNumber = FormatterSingleton.INSTANCE.phoneInstance().parse(phone, FORMAT_TO_US);
            return FormatterSingleton.INSTANCE.phoneInstance().format(phoneNumber, PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            LOG.warn("Failed parsing phone number={} reason={}", phone, e.getLocalizedMessage(), e);
            return StringUtils.EMPTY;
        }
    }

    public static String toSmallDate(Date date) {
        return SDF_SMALL.format(date);
    }
}
