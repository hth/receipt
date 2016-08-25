package com.receiptofi.domain;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 8/18/16 10:20 AM
 */
public class CampaignStatsEntity {
    /** Number of coupon to release. */
    @Field ("DP")
    private int distributionPercent;

    /** Number of coupon distributed. Computed by system. */
    @Field ("DS")
    private int distributionSuccess;

    /** Number of coupon distributed. Computed by system. */
    @Field ("DF")
    private int distributionFailure;

    /** Number of coupon distributed. Computed by system. */
    @Field ("DK")
    private int distributionSkipped;

    public CampaignStatsEntity(int distributionPercent) {
        this.distributionPercent = distributionPercent;
    }

    public int getDistributionPercent() {
        return distributionPercent;
    }

    public void setDistributionPercent(int distributionPercent) {
        this.distributionPercent = distributionPercent;
    }

    public int getDistributionSuccess() {
        return distributionSuccess;
    }

    public void setDistributionSuccess(int distributionSuccess) {
        this.distributionSuccess = distributionSuccess;
    }

    public int getDistributionFailure() {
        return distributionFailure;
    }

    public void setDistributionFailure(int distributionFailure) {
        this.distributionFailure = distributionFailure;
    }

    public int getDistributionSkipped() {
        return distributionSkipped;
    }

    public void setDistributionSkipped(int distributionSkipped) {
        this.distributionSkipped = distributionSkipped;
    }
}
