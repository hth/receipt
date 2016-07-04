package com.receiptofi.domain.flow;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: hitender
 * Date: 6/10/16 12:04 PM
 */
public class CouponCampaign implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CouponCampaign.class);
    private static final long serialVersionUID = 5707616575086343836L;

    private String campaignId;
    private String rid;
    private String bizId;

    @Transient
    private String businessName;
    private ScrubbedInput freeText;
    private ScrubbedInput additionalInfo;
    private String start;
    private String end;
    private String live;
    private String distributionPercent;
    private CampaignStatusEnum campaignStatus;
    private Collection<FileSystemEntity> fileSystemEntities;
    //TODO(hth) add reason to show up for rejection of campaign, add it in BusinessCampaignEntity

    @SuppressWarnings ("unused")
    private CouponCampaign() {
    }

    public CouponCampaign(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public CouponCampaign setCampaignId(String campaignId) {
        this.campaignId = campaignId;
        return this;
    }

    public String getRid() {
        return rid;
    }

    public CouponCampaign setRid(String rid) {
        this.rid = rid;
        return this;
    }

    public String getBizId() {
        return bizId;
    }

    public CouponCampaign setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public CouponCampaign setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public ScrubbedInput getFreeText() {
        return freeText;
    }

    public CouponCampaign setFreeText(ScrubbedInput freeText) {
        this.freeText = freeText;
        return this;
    }

    public ScrubbedInput getAdditionalInfo() {
        return additionalInfo;
    }

    public CouponCampaign setAdditionalInfo(ScrubbedInput additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public String getStart() {
        return start;
    }

    public CouponCampaign setStart(String start) {
        this.start = start;
        return this;
    }

    public String getEnd() {
        return end;
    }

    public CouponCampaign setEnd(String end) {
        this.end = end;
        return this;
    }

    public String getLive() {
        return live;
    }

    public CouponCampaign setLive(String live) {
        this.live = live;
        return this;
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @return
     */
    @SuppressWarnings ("unused")
    @Transient
    public int getDaysBetween() {
        return DateUtil.getDaysBetween(start, end);
    }

    public String getDistributionPercent() {
        return distributionPercent;
    }

    public CouponCampaign setDistributionPercent(String distributionPercent) {
        this.distributionPercent = distributionPercent;
        return this;
    }

    public int getDistributionPercentAsInt() {
        if (StringUtils.isNotBlank(distributionPercent)) {
            return Integer.parseInt(distributionPercent.substring(0, distributionPercent.length() - 1));
        } else {
            return 0;
        }
    }

    public CampaignStatusEnum getCampaignStatus() {
        return campaignStatus;
    }

    public CouponCampaign setCampaignStatus(CampaignStatusEnum campaignStatus) {
        this.campaignStatus = campaignStatus;
        return this;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public CouponCampaign setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
        return this;
    }
}
