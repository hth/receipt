package com.receiptofi.domain.flow;

import com.receiptofi.domain.CampaignStatsEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.domain.types.CampaignTypeEnum;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private CampaignStatusEnum campaignStatus;
    private Collection<FileSystemEntity> fileSystemEntities;

    private String distributionPercentPatrons;
    private String distributionPercentNonPatrons;

    private String reason;

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

    public String getDistributionPercentPatrons() {
        return distributionPercentPatrons;
    }

    public CouponCampaign setDistributionPercentPatrons(String distributionPercentPatrons) {
        this.distributionPercentPatrons = distributionPercentPatrons;
        return this;
    }

    private int getDistributionPercentLocalAsInt() {
        if (StringUtils.isNotBlank(distributionPercentPatrons)) {
            return Integer.parseInt(distributionPercentPatrons.substring(0, distributionPercentPatrons.length() - 1));
        } else {
            return 0;
        }
    }

    public String getDistributionPercentNonPatrons() {
        return distributionPercentNonPatrons;
    }

    public CouponCampaign setDistributionPercentNonPatrons(String distributionPercentNonPatrons) {
        this.distributionPercentNonPatrons = distributionPercentNonPatrons;
        return this;
    }

    private int getDistributionPercentAllAsInt() {
        if (StringUtils.isNotBlank(distributionPercentNonPatrons)) {
            return Integer.parseInt(distributionPercentNonPatrons.substring(0, distributionPercentNonPatrons.length() - 1));
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Transient
    public Map<String, CampaignStatsEntity> getCampaignStats() {
        Map<String, CampaignStatsEntity> campaignStats = new LinkedHashMap<>();
        campaignStats.put(CampaignTypeEnum.P.getName(), new CampaignStatsEntity(getDistributionPercentLocalAsInt()));
        campaignStats.put(CampaignTypeEnum.NP.getName(), new CampaignStatsEntity(getDistributionPercentAllAsInt()));
        return campaignStats;
    }
}
