package com.receiptofi.web.form;

import java.util.Iterator;
import java.util.List;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.web.helper.ReceiptForMonth;

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

    /** Receipts grouped by days. Used in showing in Calendar */
    private Iterator<ReceiptGrouped> receiptGrouped;
    private List<LandingDonutChart> bizByExpenseTypes;
    private String bizNames;

    List<NotificationEntity> notifications;

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

    public Iterator<ReceiptGrouped> getReceiptGrouped() {
        return receiptGrouped;
    }

    public void setReceiptGrouped(Iterator<ReceiptGrouped> receiptGrouped) {
        this.receiptGrouped = receiptGrouped;
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

    public List<NotificationEntity> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationEntity> notifications) {
        this.notifications = notifications;
    }
}

