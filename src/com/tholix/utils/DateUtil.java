/**
 *
 */
package com.tholix.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author hitender
 * @when Dec 27, 2012 2:53:16 PM
 *
 */
public final class DateUtil {
	private static final Logger log = Logger.getLogger(DateUtil.class);

    public static final int SECOND = 1;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = MINUTE * MINUTE;
    public static final int DAY = HOUR * 24;

	private enum DateType {
		FRM_1("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}(PM|AM)", 				"12/15/2012 02:13PM", 		"MM/dd/yyyy hh:mma"),
		FRM_2("\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}", 					"12/24/12 19:03", 			"MM/dd/yy kk:mm"),
		FRM_3("\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}:\\d{2}", 				"12/25/12 16:54:57", 		"MM/dd/yy kk:mm:ss"),
		FRM_4("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}", 				"12/15/2012 16:46:53", 		"MM/dd/yyyy kk:mm:ss"),
		FRM_5("\\d{1,2}/\\d{1,2}/\\d{4}\\s(PM|AM)\\s\\d{1,2}:\\d{2}:\\d{2}", 	"12/15/2012 PM 04:49:45", 	"MM/dd/yyyy a hh:mm:ss"),
		FRM_6("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}\\s(PM|AM)", 			"08/29/2012 03:07 PM", 		"MM/dd/yyyy hh:mm a"),
		FRM_7("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s(PM|AM)",	"12/26/2012 5:29:44 PM", 	"MM/dd/yyyy hh:mm:ss a"),
		;

		private final String regex;
		private final String example;
		private final DateTimeFormatter formatter;

		private DateType(String regex, String example, String formatter) {
			this.regex = regex;
			this.example = example;
			this.formatter = DateTimeFormat.forPattern(formatter);
		}

		public String getRegex() {
			return regex;
		}

		@SuppressWarnings("unused")
		public String getExample() {
			return example;
		}

		public DateTimeFormatter getFormatter() {
			return formatter;
		}
	}

	/**
	 * Support various kinds of date format
	 * 12/15/2012 02:13PM
	 * 12/24/12 19:03
	 * 12/25/12 16:54:57
	 * 12-15-2012 16:46:53
	 * 12/15/2012 PM 04:49:45
	 * 08/29/2012 03:07 PM
	 *
	 * @param dateString
	 * @return
	 */
	public static Date getDateFromString(String dateString) throws Exception {
		dateString = StringUtils.trim(dateString.replaceAll("-", "/"));
		for (DateType dateType : DateType.values()) {
			if (dateString.matches(dateType.getRegex())) {
				return DateTime.parse(dateString, dateType.getFormatter()).toDate();
			}
		}

		log.error("Unsupported date condition reached: Not supported date string : " + dateString);
		throw new Exception("Unsupported date condition reached: Not supported date string : " + dateString);
	}

    /**
     *
     * @return DateTime of type Joda Time
     */
    public static DateTime now() {
        return DateTime.now();
    }

    public static Date nowTime() {
        return now().toDate();
    }

    public static String nowTimeString() {
        return nowTime().toString();
    }

    /**
     * Gets the current duration of the process
     * @param start
     * @return seconds
     */
    public static Seconds duration(DateTime start) {
        Duration duration = new Duration(start.getMillis(), now().getMillis());
        return duration.toStandardSeconds();
    }

    /**
     * Gets the duration of the process between two end points
     * @param start
     * @param end
     * @return seconds
     */
    public static long duration(DateTime start, DateTime end) {
        Duration duration = new Duration(start.getMillis(), end.getMillis());
        return duration.getMillis();
    }


    /**
     * Time in seconds, minutes, hours, days. Does not support precision.
     *
     * @param date
     * @return
     */
    public static String getDurationStr(Date date) {
        int time = (DateUtil.duration(new DateTime(date)).getSeconds());
        if(time < DateUtil.MINUTE) {
            return time + " Seconds";

        } else if(time/DateUtil.MINUTE < DateUtil.MINUTE) {
            return time/DateUtil.MINUTE + " Minutes";

        } else if(time/DateUtil.HOUR < DateUtil.HOUR) {
            return time/DateUtil.HOUR + " Hours";

        } else {
            return time/DateUtil.DAY + " Days";
        }
    }
}
