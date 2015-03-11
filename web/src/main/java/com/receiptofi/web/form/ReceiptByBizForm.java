package com.receiptofi.web.form;

import com.receiptofi.web.helper.ReceiptLandingView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2/28/15 3:12 AM
 */
public class ReceiptByBizForm {
    private List<ReceiptLandingView> receiptLandingViews = new ArrayList<>();
    private String bizName;
    private String monthYear;

    public List<ReceiptLandingView> getReceiptLandingViews() {
        return receiptLandingViews;
    }

    public String getBizNameForTitle() {
        return StringUtils.abbreviate(bizName, 18);
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
}
