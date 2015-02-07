package com.receiptofi.web.form;

import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptListView;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2/5/15 7:44 PM
 */
public class ReportAnalysisForm {
    private List<ReceiptGrouped> receiptGroupedByMonths = new LinkedList<>();
    private List<ReceiptListView> receiptListViews = new LinkedList<>();

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
}
