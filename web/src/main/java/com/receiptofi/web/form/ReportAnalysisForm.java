package com.receiptofi.web.form;

import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptListView;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 2/5/15 7:44 PM
 */
public class ReportAnalysisForm {
    private List<ReceiptGrouped> receiptGroupedByMonths = new LinkedList<>();
    private List<ReceiptListView> receiptListViews = new LinkedList<>();
    private Map<String, BigDecimal> itemExpenses;
    private int itemsForYear;

    public List<ReceiptGrouped> getReceiptGroupedByMonths() {
        return receiptGroupedByMonths;
    }

    public void setReceiptGroupedByMonths(List<ReceiptGrouped> receiptGroupedByMonths) {
        this.receiptGroupedByMonths = receiptGroupedByMonths;
    }

    public List<ReceiptListView> getReceiptListViews() {
        return receiptListViews;
    }

    public void setReceiptListViews(List<ReceiptListView> receiptListViews) {
        this.receiptListViews = receiptListViews;
    }

    public int getItemsForYear() {
        return itemsForYear;
    }

    /**
     * Set year formatted as 'YYYY'
     * @param itemsForYear
     */
    public void setItemsForYear(int itemsForYear) {
        this.itemsForYear = itemsForYear;
    }

    public Map<String, BigDecimal> getItemExpenses() {
        return itemExpenses;
    }

    public void setItemExpenses(Map<String, BigDecimal> itemExpenses) {
        this.itemExpenses = itemExpenses;
    }
}
