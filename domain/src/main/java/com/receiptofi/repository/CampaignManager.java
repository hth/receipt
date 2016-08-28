package com.receiptofi.repository;

import com.receiptofi.domain.CampaignEntity;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 6/10/16 4:30 PM
 */
public interface CampaignManager extends RepositoryManager<CampaignEntity> {

    CampaignEntity findById(String campaignId, String bizId);

    /**
     * Note: Should be accessed by UserLevelEnum SUPERVISOR or TECH_CAMPAIGN.
     *
     * @param campaignId
     * @return
     */
    CampaignEntity findById(String campaignId, UserLevelEnum userLevel);

    List<CampaignEntity> findBy(String bizId);

    List<CampaignEntity> findAllPendingApproval(int limit);

    long countPendingApproval();

    /**
     * Note: Should be accessed by UserLevelEnum SUPERVISOR or TECH_CAMPAIGN.
     *
     * @param campaignId
     * @param validateByRid
     * @param userLevel
     * @param campaignStatus
     */
    void updateCampaignStatus(
            String campaignId,
            String validateByRid,
            UserLevelEnum userLevel,
            CampaignStatusEnum campaignStatus,
            String reason);

    /**
     * Finds all approved campaign.
     *
     * @param limit
     * @return
     */
    List<CampaignEntity> findCampaignWithStatus(int limit, CampaignStatusEnum campaignStatus, Date since);
}
