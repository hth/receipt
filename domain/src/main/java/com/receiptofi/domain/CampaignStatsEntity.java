package com.receiptofi.domain;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 8/18/16 10:20 AM
 */
public class CampaignStatsEntity {
    /** Number of coupon to release. */
    @Field ("P")
    private int distributionPercent;

    /** Number of coupon distributed. Computed by system. */
    @Field ("S")
    private int distributionSuccess;

    /** Number of coupon distributed. Computed by system. */
    @Field ("F")
    private int distributionFailure;

    /** Number of coupon distributed. Computed by system. */
    @Field ("K")
    private int distributionSkipped;

    /** Default Radius for distributing campaign. */
    @Field ("R")
    private int distributionRadius = 3;

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

    public int getDistributionRadius() {
        return distributionRadius;
    }

    public void setDistributionRadius(int distributionRadius) {
        this.distributionRadius = distributionRadius;
    }
}
