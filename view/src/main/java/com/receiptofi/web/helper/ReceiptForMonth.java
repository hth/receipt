package com.receiptofi.web.helper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Used as JSON and also as Object representation for Landing Page to show receipt data for that month.
 * User: hitender
 * Date: 7/6/13
 * Time: 12:18 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ReceiptForMonth {
    public static final DateTimeFormatter MMM_YYYY = DateTimeFormat.forPattern("MMM, yyyy");

    private DateTime monthYearDateTime;
    private List<ReceiptLandingView> receipts = new ArrayList<>();

    private ReceiptForMonth() {
    }

    public static ReceiptForMonth newInstance() {
        return new ReceiptForMonth();
    }

    public String getMonthYear() {
        return MMM_YYYY.print(monthYearDateTime);
    }

    public String getYear() {
        return MMM_YYYY.print(monthYearDateTime).substring(5, MMM_YYYY.print(monthYearDateTime).length());
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
