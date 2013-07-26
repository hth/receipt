package com.tholix.web.form;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 5/25/13
 * Time: 11:17 AM
 */
public final class LandingDonutChart {
    public static final int OFF_SET     = 0;
    public static final int MAX_WIDTH   = 8;

    private String bizName;
    private BigDecimal total;
    private String expenseTypes;
    private String expenseValues;

    @SuppressWarnings("unused")
    private LandingDonutChart() {}

    private LandingDonutChart(String bizName) {
        this.bizName = bizName;
    }

    public static LandingDonutChart newInstance(String bizName) {
        return new LandingDonutChart(bizName);
    }

    public String getBizName() {
        return bizName;
    }

    public String getShortenedBizName4Display() {
        return StringUtils.abbreviate(bizName, OFF_SET, MAX_WIDTH);
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
