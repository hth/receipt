package com.receiptofi.repository;

import com.receiptofi.domain.CouponEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 4/27/16 11:59 PM
 */
public interface CouponManager extends RepositoryManager<CouponEntity> {
    CouponEntity findOne(String id);

    List<CouponEntity> findCouponToUpload(int limit);

    void cloudUploadSuccessful(String id, String imagePathOnCloud);

    /**
     * Campaign coupons are marked inactive so that system removes them from distribution.
     *
     * @param campaignId
     */
    void markCampaignCouponsInactive(String campaignId);
}
