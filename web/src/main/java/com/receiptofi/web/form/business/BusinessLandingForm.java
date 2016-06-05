package com.receiptofi.web.form.business;

import java.math.BigDecimal;

/**
 * User: hitender
 * Date: 6/3/16 4:06 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class BusinessLandingForm {

    private String bizName;
    private long customerCount;
    private BigDecimal totalCustomerPurchases;
    private long storeCount;
    private long actualStoreCount;

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public long getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(long customerCount) {
        this.customerCount = customerCount;
    }

    public BigDecimal getTotalCustomerPurchases() {
        return totalCustomerPurchases;
    }

    public void setTotalCustomerPurchases(BigDecimal totalCustomerPurchases) {
        this.totalCustomerPurchases = totalCustomerPurchases;
    }

    public long getStoreCount() {
        return storeCount;
    }

    public void setStoreCount(long storeCount) {
        this.storeCount = storeCount;
    }

    public long getActualStoreCount() {
        return actualStoreCount;
    }

    public void setActualStoreCount(long actualStoreCount) {
        this.actualStoreCount = actualStoreCount;
    }
}
