/**
 *
 */
package com.receiptofi.domain.value;

import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.format.annotation.NumberFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hitender
 * @since Jan 12, 2013 6:25:15 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class ReceiptGrouped implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptGrouped.class);

    private static final SimpleDateFormat FULL_CALENDAR_SDF = new SimpleDateFormat("yyyy-MM-dd");

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private BigDecimal splitTotal;

    private int year;
    private int month;
    private int day;

    /**
     * Used by mongo groupBy method
     */
    @SuppressWarnings ("unused")
    private ReceiptGrouped() {
        super();
    }

    private ReceiptGrouped(BigDecimal splitTotal, int year, int month, int day) {
        super();
        this.splitTotal = splitTotal;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static ReceiptGrouped newInstance(BigDecimal splitTotal, int year, int month, int day) {
        return new ReceiptGrouped(splitTotal, year, month, day);
    }

    /**
     * Used in the Calendar for display. Helps scale the total number computed from GroupBy
     *
     * @return
     */
    @SuppressWarnings ("unused")
    public BigDecimal getStringTotal() {
        return splitTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    //TODO: Note day should not be zero other wise gets an exception while setting the date with zero. May remove this code
    public Date getDate() {
        if (year == 0 || month == 0 || day == 0) {
            //This should never happen. Add validation in receipt during save.
            LOG.error("Setting now time as --> Year or month or day should not be zero. Year " + year + ", month: " + month + ", day: " + day);
            return DateUtil.now().toDate();
        }
        return new DateTime(year, month, day, 0, 0).toDate();
    }


    @SuppressWarnings ("unused")
    public String getDateForFullCalendar() {
        return FULL_CALENDAR_SDF.format(getDate());
    }

    public DateTime getDateTime() {
        if (year == 0 || month == 0) {
            ////This should never happen. Add validation in receipt during save.
            LOG.error("Setting now time as --> Year and month should not be zero. Year " + year + ", month: " + month);
            return DateUtil.now();
        }
        return new DateTime(year, month, 1, 0, 0);
    }

    /**
     * Used in display monthly expense bar name in bar chart
     *
     * @return
     */
    @SuppressWarnings ("unused")
    public String getMonthName() {
        return getDateTime().toString("MMM yyyy");
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public BigDecimal getSplitTotal() {
        return Maths.adjustScale(splitTotal);
    }

    @Override
    public String toString() {
        return "ReceiptGrouped{" +
                "splitTotal=" + splitTotal +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }
}
