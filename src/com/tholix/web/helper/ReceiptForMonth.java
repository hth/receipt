package com.tholix.web.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Since;

/**
 * Used as JSON and also as Object representation for Landing Page to show receipt data for that month
 *
 * User: hitender
 * Date: 7/6/13
 * Time: 12:18 PM
 */
public class ReceiptForMonth {

    @Since(1.0)
    private String monthYear;
    private List<ReceiptLandingView> receipts = new ArrayList<>();

    private ReceiptForMonth() {}

    public static ReceiptForMonth newInstance() {
        return new ReceiptForMonth();
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public List<ReceiptLandingView> getReceipts() {
        return receipts;
    }

    public void addReceipt(ReceiptLandingView receipt) {
        this.receipts.add(receipt);
    }
}
