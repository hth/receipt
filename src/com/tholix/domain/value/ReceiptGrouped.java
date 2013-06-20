/**
 *
 */
package com.tholix.domain.value;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

/**
 * @author hitender
 * @since Jan 12, 2013 6:25:15 PM
 *
 */
public final class ReceiptGrouped implements Serializable {
	private static final long serialVersionUID = 291731832249108585L;
    private static volatile Logger log = Logger.getLogger(ReceiptGrouped.class);

    @SuppressWarnings("unused") private Double total;
    @SuppressWarnings("unused") private int year;
    @SuppressWarnings("unused")	private int month;
    @SuppressWarnings("unused") private int day;
    @SuppressWarnings("unused") private Date date;

	private ReceiptGrouped() {

	}

	public Double getTotal() {
		return total;
	}

	public Date getDate() {
		return new DateTime(year, month, day, 0, 0).toDate();
	}

    private DateTime getDateTime() {
        DateTime date = new DateTime(year, month, 1, 0, 0);
        return date;
    }

    public String getMonthName() {
        return getDateTime().toString("MMM");
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

    public int sumOfYearMonthDay() {
        return this.year + this.month + this.day;
    }

    @Override
	public String toString() {
		return new StringBuilder().append("ReceiptGrouped [total=")
                .append(total).append(", year=")
                .append(year).append(", month=")
                .append(month).append(", day=")
                .append(day).append("]")
                .toString();
	}
}
