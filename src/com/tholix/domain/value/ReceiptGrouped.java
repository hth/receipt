/**
 *
 */
package com.tholix.domain.value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

/**
 * @author hitender
 * @since Jan 12, 2013 6:25:15 PM
 *
 */
public final class ReceiptGrouped implements Serializable {
	private static final long serialVersionUID = 291731832249108585L;
    private static volatile Logger log = Logger.getLogger(ReceiptGrouped.class);

    @SuppressWarnings("unused") private BigDecimal total;
    @SuppressWarnings("unused") private int year;
    @SuppressWarnings("unused")	private int month;
    @SuppressWarnings("unused") private int day;

	private ReceiptGrouped() {}

    /**
     * Used in the Calendar for display. Helps scale the total number computed from GroupBy
     *
     * @return
     */
    public BigDecimal getStringTotal() {
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

	public Date getDate() {
		return new DateTime(year, month, day, 0, 0).toDate();
	}

    private DateTime getDateTime() {
        DateTime date = new DateTime(year, month, 1, 0, 0);
        return date;
    }

    public String getMonthName() {
        return getDateTime().toString("MMM-yy");
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

    public long dateInMillisForSorting() {
        return new DateTime(year, month, 1, 0, 0).getMillis();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("total", total)
                .add("year", year)
                .add("month", month)
                .add("day", day)
                .toString();
    }
}
