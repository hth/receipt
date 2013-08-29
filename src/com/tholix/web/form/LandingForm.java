package com.tholix.web.form;

import java.util.List;

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
}

