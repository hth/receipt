package com.receiptofi.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Used as JSON and also as Object representation for Landing Page to show receipt data for that month.
 * User: hitender
 * Date: 7/6/13
 * Time: 12:18 PM
 */
public final class ReceiptForMonth {
    private static DateTimeFormatter dtf = DateTimeFormat.forPattern("MMM, yyyy");

    private DateTime monthYearDateTime;
    private List<ReceiptLandingView> receipts = new ArrayList<>();

    private ReceiptForMonth() {
    }

    public static ReceiptForMonth newInstance() {
        return new ReceiptForMonth();
    }

    public String getMonthYear() {
        return dtf.print(monthYearDateTime);
    }

    public String getYear() {
        return dtf.print(monthYearDateTime).substring(5, dtf.print(monthYearDateTime).length());
    }

    public Date getMonthYearDateTime() {
        return monthYearDateTime.toDate();
    }

    public void setMonthYearDateTime(DateTime monthYearDateTime) {
        this.monthYearDateTime = monthYearDateTime;
    }

    public List<ReceiptLandingView> getReceipts() {
        return receipts;
    }

    public void addReceipt(ReceiptLandingView receipt) {
        this.receipts.add(receipt);
    }
}
