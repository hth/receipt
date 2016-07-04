package com.receiptofi.web.form.business;

import com.receiptofi.domain.CampaignEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 6/26/16 11:35 AM
 */
public class CampaignListForm {

    private long campaignCount;
    private List<CampaignEntity> businessCampaigns;

    public long getCampaignCount() {
        return campaignCount;
    }

    public CampaignListForm setCampaignCount(long campaignCount) {
        this.campaignCount = campaignCount;
        return this;
    }

    public List<CampaignEntity> getBusinessCampaigns() {
        return businessCampaigns;
    }

    public CampaignListForm setBusinessCampaigns(List<CampaignEntity> businessCampaigns) {
        this.businessCampaigns = businessCampaigns;
        return this;
    }
}
