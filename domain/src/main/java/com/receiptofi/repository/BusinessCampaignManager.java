package com.receiptofi.repository;

import com.receiptofi.domain.BusinessCampaignEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 6/10/16 4:30 PM
 */
public interface BusinessCampaignManager extends RepositoryManager<BusinessCampaignEntity> {

    BusinessCampaignEntity findById(String campaignId, String bizId);

    List<BusinessCampaignEntity> findBy(String bizId);
}
