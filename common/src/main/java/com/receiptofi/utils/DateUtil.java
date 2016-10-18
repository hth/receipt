/**
 *
 */
package com.receiptofi.utils;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.PeriodType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

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
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    private static final int MINUTE_IN_SECONDS = 60;
    private static final int HOUR_IN_SECONDS = MINUTE_IN_SECONDS * MINUTE_IN_SECONDS;
    public static final int HOURS = 24;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    private static final DateTimeFormatter DF_MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

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
        LOG.info("Supplied date={}", dateAsStr);

        if (StringUtils.isNotBlank(dateAsStr)) {
            String date = StringUtils.trim(dateAsStr.trim().toUpperCase().replaceAll("-", "/")).replaceAll("[\\t\\n\\r]+", " ");
            for (DateTypeWithTime dateType : DateTypeWithTime.values()) {
                if (date.matches(dateType.getRegex())) {
                    LOG.debug("DateTypeWithTime={} regex={} example={}", dateType.name(), dateType.regex, dateType.example);
                    return convertToDateTime(date, dateType.getFormatter());
                }
            }

            for (DateTypeWithoutTime dateType : DateTypeWithoutTime.values()) {
                if (date.matches(dateType.getRegex())) {
                    LOG.debug("DateTypeWithTime={} regex={} example={}", dateType.name(), dateType.regex, dateType.example);
                    return convertToDate(date, dateType.getFormatter());
                }
            }
        }

        LOG.warn("Unsupported date condition reached='{}'", dateAsStr);
        throw new IllegalArgumentException("Unsupported date condition reached " + dateAsStr);
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

    public static Date nowDate() {
        return now().toDate();
    }

    public static Date nowMidnightDate() {
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

    public static long getDuration(Date begin, Date end) {
        return Duration.between(begin.toInstant(), end.toInstant()).getSeconds();
    }

    public static Date getDateMinusMinutes(int minutes) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(minutes);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date getDateMinusDay(long days) {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(days);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Converts age to birthday.
     *
     * @param age any number greater than zero
     * @return start of the year as birthday
     */
    private static String covertAgeToBirthday(String age) {
        long years = Long.parseLong(age);
        if (years <= 0) {
            return "";
        }

        LocalDate localDate = LocalDate.now().minusYears(years);
        localDate = localDate.with(TemporalAdjusters.firstDayOfYear());
        return localDate.format(DF_MM_DD_YYYY);
    }

    public static String parseAgeForBirthday(String age) {
        String birthday = "";
        if (StringUtils.isNotBlank(age)) {
            if (age.contains("-")) {
                String[] range = age.split("-");
                birthday = covertAgeToBirthday(range[0]);
            } else {
                birthday = covertAgeToBirthday(age);
            }
        }
        return birthday;
    }

    /**
     * Gets current time on UTC. This is required when setting up cron task as server time is set on UTC.
     *
     * @return
     */
    public static Date getUTCDate() {
        return new DateTime(DateTimeZone.UTC).toLocalDateTime().toDate();
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDaysBetween(String start, String end) {
        Assert.isTrue(StringUtils.isNotBlank(start), "Start date string is null");
        Assert.notNull(StringUtils.isNotBlank(end), "End date string is null");
        return getDaysBetween(convertToDate(start), convertToDate(end));
    }

    public static Date convertToDate(String date) {
        return convertToDate(date, DF_MM_DD_YYYY);
    }

    private static Date convertToDate(String date, DateTimeFormatter dateTimeFormatter) {
        return convertToDate(LocalDate.parse(date, dateTimeFormatter));
    }

    private static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    private static Date convertToDateTime(String date, DateTimeFormatter dateTimeFormatter) {
        return convertToDateTime(LocalDateTime.parse(date, dateTimeFormatter));
    }

    private static Date convertToDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public static String dateToString(Date date) {
        return dateToString(date, DF_MM_DD_YYYY);
    }

    public static String dateToString(Date date, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.format(date.toInstant().atZone(ZoneOffset.UTC));
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDaysBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.days()).getDays();
    }

    public static int getMillisBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.millis()).getMillis();
    }

    public static int getSecondsBetween(Date start, Date end) {
        return getMillisBetween(start, end) / 1000;
    }

    public static int getMinuteBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.minutes()).getMinutes();
    }

    /* Date string should have time appended since its a DateTimeFormatter. */
    public enum DateTypeWithTime {
        DT1("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s(AM|PM)",
                "01/01/2016 03:03:03 AM",
                "MM/dd/yyyy hh:mm:ss a"),

        DT101("\\d{2}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}\\s(AM|PM)",
                "01/01/2016 3:03:03 AM",
                "MM/dd/yyyy h:mm:ss a"),

        DT102("\\d{2}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}",
                "01/01/2016 3:03:03",
                "MM/dd/yyyy k:mm:ss"),

        DT2("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}",
                "01/01/2016 23:03:03",
                "MM/dd/yyyy kk:mm:ss"),

        DT3("\\d{1}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s(AM|PM)",
                "1/01/2016 03:03:03 AM",
                "M/dd/yyyy hh:mm:ss a"),

        DT301("\\d{1}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}\\s(AM|PM)",
                "1/01/2016 3:03:03 AM",
                "M/dd/yyyy h:mm:ss a"),

        DT302("\\d{1}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}",
                "1/01/2016 3:03:03",
                "M/dd/yyyy k:mm:ss"),

        DT4("\\d{1}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}",
                "1/01/2016 23:03:03",
                "M/dd/yyyy kk:mm:ss"),

        DT5("\\d{2}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s(AM|PM)",
                "01/1/2016 03:03:03 AM",
                "MM/d/yyyy hh:mm:ss a"),

        DT501("\\d{2}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}\\s(AM|PM)",
                "01/1/2016 3:03:03 AM",
                "MM/d/yyyy h:mm:ss a"),

        DT502("\\d{2}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}",
                "01/1/2016 3:03:03",
                "MM/d/yyyy k:mm:ss"),

        DT6("\\d{2}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}",
                "01/1/2016 23:03:03",
                "MM/d/yyyy kk:mm:ss"),

        DT7("\\d{1}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s(AM|PM)",
                "1/1/2016 03:03:03 AM",
                "M/d/yyyy hh:mm:ss a"),

        DT701("\\d{1}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}\\s(AM|PM)",
                "1/1/2016 3:03:03 AM",
                "M/d/yyyy h:mm:ss a"),

        DT702("\\d{1}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}:\\d{2}",
                "1/1/2016 3:03:03",
                "M/d/yyyy k:mm:ss"),

        DT8("\\d{1}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}",
                "1/1/2016 23:03:03",
                "M/d/yyyy kk:mm:ss"),

        DT9("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s(AM|PM)",
                "01/01/2016 03:03 AM",
                "MM/dd/yyyy hh:mm a"),

        DT901("\\d{2}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}\\s(AM|PM)",
                "01/01/2016 3:03 AM",
                "MM/dd/yyyy h:mm a"),

        DT902("\\d{2}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}",
                "01/01/2016 3:03",
                "MM/dd/yyyy k:mm"),

        DT10("\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}",
                "01/01/2016 23:03",
                "MM/dd/yyyy kk:mm"),

        DT11("\\d{1}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s(AM|PM)",
                "1/01/2016 03:03 AM",
                "M/dd/yyyy hh:mm a"),

        DT1101("\\d{1}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}\\s(AM|PM)",
                "1/01/2016 3:03 AM",
                "M/dd/yyyy h:mm a"),

        DT1102("\\d{1}/\\d{2}/\\d{4}\\s\\d{1}:\\d{2}",
                "1/01/2016 3:03",
                "M/dd/yyyy k:mm"),

        DT12("\\d{1}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}",
                "1/01/2016 23:03",
                "M/dd/yyyy kk:mm"),

        DT13("\\d{2}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}\\s(AM|PM)",
                "01/1/2016 03:03 AM",
                "MM/d/yyyy hh:mm a"),

        DT1301("\\d{2}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}\\s(AM|PM)",
                "01/1/2016 3:03 AM",
                "MM/d/yyyy h:mm a"),

        DT1302("\\d{2}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}",
                "01/1/2016 3:03",
                "MM/d/yyyy k:mm"),

        DT14("\\d{2}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}",
                "01/1/2016 23:03",
                "MM/d/yyyy kk:mm"),

        DT15("\\d{1}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}\\s(AM|PM)",
                "1/1/2016 03:03 AM",
                "M/d/yyyy hh:mm a"),

        DT1501("\\d{1}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}\\s(AM|PM)",
                "1/1/2016 3:03 AM",
                "M/d/yyyy h:mm a"),

        DT1502("\\d{1}/\\d{1}/\\d{4}\\s\\d{1}:\\d{2}",
                "1/1/2016 3:03",
                "M/d/yyyy k:mm"),

        DT16("\\d{1}/\\d{1}/\\d{4}\\s\\d{2}:\\d{2}",
                "1/1/2016 23:03",
                "M/d/yyyy kk:mm");

        private final String regex;

        private final String example;

        private final DateTimeFormatter formatter;

        DateTypeWithTime(String regex, String example, String formatter) {
            this.regex = regex;
            this.example = example;
            this.formatter = DateTimeFormatter.ofPattern(formatter, Locale.US);
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

    public enum DateTypeWithoutTime {

        DT1701("\\d{1}/\\d{1}/\\d{4}",
                "1/1/2016",
                "M/d/yyyy"),

        DT1702("\\d{2}/\\d{2}/\\d{4}",
                "1/1/2016",
                "MM/dd/yyyy"),

        DT1703("\\d{2}/\\d{1}/\\d{4}",
                "01/1/2016",
                "MM/d/yyyy"),

        DT1704("\\d{1}/\\d{2}/\\d{4}",
                "1/01/2016",
                "M/dd/yyyy");

        private final String regex;

        private final String example;

        private final DateTimeFormatter formatter;

        DateTypeWithoutTime(String regex, String example, String formatter) {
            this.regex = regex;
            this.example = example;
            this.formatter = DateTimeFormatter.ofPattern(formatter, Locale.US);
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
