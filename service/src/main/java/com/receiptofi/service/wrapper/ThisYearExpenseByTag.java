package com.receiptofi.service.wrapper;

import java.math.BigDecimal;

/**
 * Report analysis to show expense by tag in pie chart.
 *
 * User: hitender
 * Date: 2/26/15 11:24 PM
 */
public class ThisYearExpenseByTag {
    private String tagName;
    private String tagColor;
    private String tagIcon;
    private BigDecimal total;
    private BigDecimal percentage;

    public ThisYearExpenseByTag(String tagName, String tagColor, String tagIcon, BigDecimal total) {
        this.tagName = tagName;
        this.tagColor = tagColor;
        this.tagIcon = tagIcon;
        this.total = total;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public String getTagIcon() {
        return tagIcon;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
