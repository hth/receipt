package com.tholix.web.form;

import java.util.List;

import com.tholix.domain.value.ReceiptGrouped;
import com.tholix.domain.value.ReceiptGroupedByBizLocation;
import com.tholix.web.helper.ReceiptForMonth;

/**
 * User: hitender
 * Date: 7/6/13
 * Time: 3:18 PM
 */
//TODO to be used and everything should be a JSON
public final class LandingForm {

    private ReceiptForMonth receiptForMonth;
    private List<ReceiptGroupedByBizLocation> receiptGroupedByBizLocations;
    private List<ReceiptGrouped> receiptGroupedByMonths;
    private List<LandingDonutChart> bizByExpenseTypes;
    private String bizNames;

    public ReceiptForMonth getReceiptForMonth() {
        return receiptForMonth;
    }

    public void setReceiptForMonth(ReceiptForMonth receiptForMonth) {
        this.receiptForMonth = receiptForMonth;
    }

    public List<ReceiptGroupedByBizLocation> getReceiptGroupedByBizLocations() {
        return receiptGroupedByBizLocations;
    }

    public void setReceiptGroupedByBizLocations(List<ReceiptGroupedByBizLocation> receiptGroupedByBizLocations) {
        this.receiptGroupedByBizLocations = receiptGroupedByBizLocations;
    }

    public List<ReceiptGrouped> getReceiptGroupedByMonths() {
        return receiptGroupedByMonths;
    }

    /**
     * Currently list receipt data for 13 months.
     * TODO Should it be increased to 7 years?
     *
     * @param receiptGroupedByMonths
     */
    public void setReceiptGroupedByMonths(List<ReceiptGrouped> receiptGroupedByMonths) {
        this.receiptGroupedByMonths = receiptGroupedByMonths;
    }

    public List<LandingDonutChart> getBizByExpenseTypes() {
        return bizByExpenseTypes;
    }

    public void setBizByExpenseTypes(List<LandingDonutChart> bizByExpenseTypes) {
        this.bizByExpenseTypes = bizByExpenseTypes;
    }

    public String getBizNames() {
        return bizNames;
    }

    public void setBizNames(String bizNames) {
        this.bizNames = bizNames;
    }
}

