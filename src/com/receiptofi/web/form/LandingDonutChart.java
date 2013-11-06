package com.receiptofi.web.form;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains data associated to be shown on donut pie chart
 *
 * User: hitender
 * Date: 5/25/13
 * Time: 11:17 AM
 */
public final class LandingDonutChart {
    public static final int OFF_SET     = 0;
    public static final int MAX_WIDTH   = 8;

    private String bizName;
    private String bizNameForId;
    private BigDecimal total;
    private String expenseTypes;
    private String expenseValues;

    @SuppressWarnings("unused")
    private LandingDonutChart() {}

    private LandingDonutChart(String bizName) {
        this.bizName = bizName;

        /** Remove all alpha numeric characters as it creates issues with 'id' */
        this.bizNameForId = StringUtils.deleteWhitespace(bizName).replaceAll("[^a-zA-Z0-9]", "");
    }

    public static LandingDonutChart newInstance(String bizName) {
        return new LandingDonutChart(bizName);
    }

    public String getBizName() {
        return bizName;
    }

    public String getBizNameForId() {
        return bizNameForId;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(String expenseTypes) {
        this.expenseTypes = expenseTypes;
    }

    public String getExpenseValues() {
        return expenseValues;
    }

    public void setExpenseValues(String expenseValues) {
        this.expenseValues = expenseValues;
    }
}