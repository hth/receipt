package com.receiptofi.web.form;

import com.receiptofi.domain.value.ReceiptGrouped;

import java.util.List;

/**
 * User: hitender
 * Date: 2/5/15 7:44 PM
 */
public class ReportAnalysisForm {
    private List<ReceiptGrouped> receiptGroupedByMonths;

    public List<ReceiptGrouped> getReceiptGroupedByMonths() {
        return receiptGroupedByMonths;
    }

    public void setReceiptGroupedByMonths(List<ReceiptGrouped> receiptGroupedByMonths) {
        this.receiptGroupedByMonths = receiptGroupedByMonths;
    }
}
