package com.receiptofi.repository;

import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.types.BusinessCampaignStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 6/10/16 4:30 PM
 */
public interface BusinessCampaignManager extends RepositoryManager<BusinessCampaignEntity> {

    BusinessCampaignEntity findById(String campaignId, String bizId);

    /**
     * Note: Should be accessed by UserLevelEnum SUPERVISOR or TECH_CAMPAIGN.
     *
     * @param campaignId
     * @return
     */
    BusinessCampaignEntity findById(String campaignId, UserLevelEnum userLevel);

    List<BusinessCampaignEntity> findBy(String bizId);

    List<BusinessCampaignEntity> findAllPendingApproval(int limit);

    long countPendingApproval();

    /**
     * Note: Should be accessed by UserLevelEnum SUPERVISOR or TECH_CAMPAIGN.
     *
     * @param campaignId
     * @param userLevel
     * @param businessCampaignStatus
     */
    void updateCampaignStatus(
            String campaignId,
            UserLevelEnum userLevel,
            BusinessCampaignStatusEnum businessCampaignStatus);

    /**
     * Finds all approved campaign.
     *
     * @param limit
     * @return
     */
    List<BusinessCampaignEntity> findCampaignWithStatus(int limit, BusinessCampaignStatusEnum businessCampaignStatus);
}
