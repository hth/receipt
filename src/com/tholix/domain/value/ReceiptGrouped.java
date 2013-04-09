/**
 *
 */
package com.tholix.domain.value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @when Jan 12, 2013 6:25:15 PM
 *
 */
public class ReceiptGrouped implements Serializable {
	private static final long serialVersionUID = 291731832249108585L;
    private static volatile Logger log = Logger.getLogger(ReceiptGrouped.class);

	private Double total;
	private int year;
	private int month;
	private int day;
	private Date date;

	public ReceiptGrouped() {

	}

	private ReceiptGrouped(Double total, Date date) {
		super();
		this.total = total;
		this.date = date;
		DateTime dateTime = new DateTime(date);
		this.year = dateTime.getYear();
		this.month = dateTime.getMonthOfYear();
		this.day = dateTime.getDayOfMonth();
	}

	private ReceiptGrouped(Double total, int year, int month, int day) {
		super();
		this.total = total;
		this.year = year;
		this.month = month;
		this.day = day;
		//year month day hour minute
		this.date = new DateTime(year, month, day, 0, 0).toDate();
	}

	public static ReceiptGrouped newInstance(Double total, int year, int month, int day) {
		return new ReceiptGrouped(total, year, month, day);
	}

	public static ReceiptGrouped newInstance(Double total, Date date) {
		return new ReceiptGrouped(total, date);
	}

	public static ReceiptGrouped newInstance(ReceiptEntity receipt) {
		return new ReceiptGrouped(receipt.getTotal(), receipt.getReceiptDate());
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "ReceiptGrouped [total=" + total + ", year=" + year + ", month=" + month + ", day=" + day + "]";
	}

	public static void getGroupedReceiptTotal(ReceiptEntity receipt, Map<Date, BigDecimal> receiptGroupedMap) {
		ReceiptGrouped rg = newInstance(receipt);
		if(receiptGroupedMap.containsKey(rg.getDate())) {
			BigDecimal total = receiptGroupedMap.get(rg.getDate());
            log.debug("Total Initial: " + total);
			total = total.add(new BigDecimal(rg.getTotal().toString()));
            log.debug("Total After addition: " + total);
			receiptGroupedMap.put(rg.getDate(), total);
		} else {
			receiptGroupedMap.put(rg.getDate(), new BigDecimal(rg.getTotal().toString()));
		}
	}
}
