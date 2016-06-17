package com.receiptofi.repository;

import com.receiptofi.domain.BusinessCampaignEntity;

/**
 * User: hitender
 * Date: 6/10/16 4:30 PM
 */
public interface BusinessCampaignManager extends RepositoryManager<BusinessCampaignEntity> {

    BusinessCampaignEntity findById(String campaignId);
}
