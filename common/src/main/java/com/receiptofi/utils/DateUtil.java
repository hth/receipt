/**
 *
 */
package com.receiptofi.utils;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author hitender
 * @since Dec 27, 2012 2:53:16 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class DateUtil {
    public static final int MINUTE_IN_SECONDS = 60;
    public static final int HOUR_IN_SECONDS = MINUTE_IN_SECONDS * MINUTE_IN_SECONDS;
    public static final int HOURS = 24;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    private DateUtil() {
    }

    /**
     * Support various kinds of date format and also trims spaces around date string.
     * 12/15/2012 02:13PM
     * 12/24/12 19:03
     * 12/25/12 16:54:57
     * 12-15-2012 16:46:53
     * 12/15/2012 PM 04:49:45
     * 08/29/2012 03:07 PM
     *
     * @param dateAsStr
     * @return
     */
    public static Date getDateFromString(String dateAsStr) {
        if (StringUtils.isNotBlank(dateAsStr)) {
            String date = StringUtils.trim(dateAsStr.trim().replaceAll("-", "/")).replaceAll("[\\t\\n\\r]+", " ");
            for (DateType dateType : DateType.values()) {
                if (date.matches(dateType.getRegex())) {
                    return DateTime.parse(date, dateType.getFormatter()).toDate();
                }
            }
        }

        LOG.error("Unsupported date condition reached: Not supported date string : " + dateAsStr);
        throw new IllegalArgumentException("Unsupported date condition reached: Not supported date string : " + dateAsStr);
    }

    /**
     * Converts java.util.Date to Joda DateTime.
     *
     * @param date
     * @return
     */
    public static DateTime toDateTime(Date date) {
        return new DateTime(date);
    }

    /** @return DateTime of type Joda Time. */
    public static DateTime now() {
        return DateTime.now();
    }

    public static Date nowTime() {
        return now().toDate();
    }

    public static Date nowDate() {
        return midnight(now()).toDate();
    }

    public static DateTime startOfYear() {
        return now().withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
    }

    public static DateTime midnight(DateTime dateTime) {
        return dateTime.withTimeAtStartOfDay();
    }

    public static Date midnight(Date date) {
        return midnight(new DateTime(date)).toDate();
    }

    //todo add support for small AM|PM
    private static enum DateType {
        FRM_1(
                "\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}(PM|AM)",
                "12/15/2012 02:13PM",
                "MM/dd/yyyy hh:mma"
        ),
        FRM_2(
                "\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}",
                "12/24/12 19:03",
                "MM/dd/yy kk:mm"
        ),
        FRM_3(
                "\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}",
                "12/24/2012 19:03",
                "MM/dd/yyyy kk:mm"
        ),
        FRM_4(
                "\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}:\\d{2}",
                "12/25/12 16:54:57",
                "MM/dd/yy kk:mm:ss"
        ),
        FRM_5(
                "\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}",
                "12/15/2012 16:46:53",
                "MM/dd/yyyy kk:mm:ss"
        ),
        FRM_6(
                "\\d{1,2}/\\d{1,2}/\\d{4}\\s(PM|AM)\\s\\d{1,2}:\\d{2}:\\d{2}",
                "12/15/2012 PM 04:49:45",
                "MM/dd/yyyy a hh:mm:ss"
        ),
        FRM_7(
                "\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}\\s(PM|AM)",
                "08/29/2012 03:07 PM",
                "MM/dd/yyyy hh:mm a"
        ),
        FRM_8(
                "\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s(PM|AM)",
                "12/26/2012 5:29:44 PM",
                "MM/dd/yyyy hh:mm:ss a"
        ),
        FRM_9(
                "\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}:\\d{2}\\s(PM|AM)",
                "12/26/12 5:29:44 PM",
                "MM/dd/yy hh:mm:ss a"
        ),
        FRM_10(
                "\\d{1,2}/\\d{1,2}/\\d{4}",
                "12/26/2012",
                "MM/dd/yyyy"
        ),
        FRM_11(
                "\\d{1,2}/\\d{1,2}/\\d{2}",
                "12/26/12",
                "MM/dd/yy"
        ),
        FRM_12(
                "\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}\\s(PM|AM)",
                "12/26/12 7:30 PM",
                "MM/dd/yy hh:mm a"
        ),
        FRM_13(
                "\\d{1,2}/\\d{1,2}/\\d{2}\\s\\d{1,2}:\\d{2}(PM|AM)",
                "12/26/12 7:30PM",
                "MM/dd/yy hh:mma"
        );

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

        @SuppressWarnings ("unused")
        public String getExample() {
            return example;
        }

        public DateTimeFormatter getFormatter() {
            return formatter;
        }
    }
}
