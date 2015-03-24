package com.receiptofi.web.form;

import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptListView;
import com.receiptofi.service.wrapper.ThisYearExpenseByTag;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2/5/15 7:44 PM
 */
public class ReportAnalysisForm {
    private List<ReceiptGrouped> receiptGroupedByMonths = new LinkedList<>();
    private List<ReceiptListView> receiptListViews = new LinkedList<>();
    private List<ThisYearExpenseByTag> thisYearExpenseByTags;
    private int itemsForYear;
    private boolean expensesForThisYear;

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

    public List<ThisYearExpenseByTag> getThisYearExpenseByTags() {
        return thisYearExpenseByTags;
    }

    public void setThisYearExpenseByTags(List<ThisYearExpenseByTag> thisYearExpenseByTags) {
        this.thisYearExpenseByTags = thisYearExpenseByTags;
    }

    public boolean isExpensesForThisYearPopulated() {
        for (ThisYearExpenseByTag thisYearExpenseByTag : thisYearExpenseByTags) {
            if (thisYearExpenseByTag.getTotal().compareTo(BigDecimal.ZERO) != 0) {
                return true;
            }
        }
        return false;
    }

    public String getTagColors() {
        StringBuilder sb = new StringBuilder();
        String colors = "";
        for(ThisYearExpenseByTag thisYearExpenseByTag : thisYearExpenseByTags) {
            sb.append(colors);
            colors = ",";
            sb.append("'").append(thisYearExpenseByTag.getTagColor()).append("'");
        }
        return sb.toString();
    }
}
