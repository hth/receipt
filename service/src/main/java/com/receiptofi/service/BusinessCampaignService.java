package com.receiptofi.service;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.flow.BusinessCampaign;
import com.receiptofi.repository.BusinessCampaignManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 6/10/16 4:30 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BusinessCampaignService {

    private BusinessCampaignManager businessCampaignManager;

    @Autowired
    public BusinessCampaignService(BusinessCampaignManager businessCampaignManager) {
        this.businessCampaignManager = businessCampaignManager;
    }

    public BusinessCampaignEntity findById(String campaignId) {
        return businessCampaignManager.findById(campaignId);
    }
}
