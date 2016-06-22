package com.receiptofi.web.form.business;

import com.receiptofi.domain.BusinessCampaignEntity;

import java.math.BigDecimal;
import java.util.List;

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
    private long storeCount;
    private BigDecimal totalCustomerPurchases;
    private long visitCount;
    private List<BusinessCampaignEntity> businessCampaigns;

    public String getBizName() {
        return bizName;
    }

    public BusinessLandingForm setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public long getCustomerCount() {
        return customerCount;
    }

    public BusinessLandingForm setCustomerCount(long customerCount) {
        this.customerCount = customerCount;
        return this;
    }

    public long getStoreCount() {
        return storeCount;
    }

    public BusinessLandingForm setStoreCount(long storeCount) {
        this.storeCount = storeCount;
        return this;
    }

    public BigDecimal getTotalCustomerPurchases() {
        return totalCustomerPurchases;
    }

    public BusinessLandingForm setTotalCustomerPurchases(BigDecimal totalCustomerPurchases) {
        this.totalCustomerPurchases = totalCustomerPurchases;
        return this;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public BusinessLandingForm setVisitCount(long visitCount) {
        this.visitCount = visitCount;
        return this;
    }

    public List<BusinessCampaignEntity> getBusinessCampaigns() {
        return businessCampaigns;
    }

    public BusinessLandingForm setBusinessCampaigns(List<BusinessCampaignEntity> businessCampaigns) {
        this.businessCampaigns = businessCampaigns;
        return this;
    }
}
