package com.receiptofi.web.form.business;

import com.receiptofi.domain.BusinessCampaignEntity;

/**
 * User: hitender
 * Date: 6/26/16 1:58 PM
 */
public class CampaignDetailForm {

    private BusinessCampaignEntity businessCampaign;

    public BusinessCampaignEntity getBusinessCampaign() {
        return businessCampaign;
    }

    public CampaignDetailForm setBusinessCampaign(BusinessCampaignEntity businessCampaign) {
        this.businessCampaign = businessCampaign;
        return this;
    }
}
