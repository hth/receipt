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

    private ScrubbedInput campaignId;
    private ScrubbedInput rid;
    private ScrubbedInput bizId;

    @Transient
    private ScrubbedInput businessName;
    private ScrubbedInput freeText;
    private ScrubbedInput additionalInfo;
    private ScrubbedInput start;
    private ScrubbedInput end;
    private ScrubbedInput live;

    private CampaignStatusEnum campaignStatus;
    private Collection<FileSystemEntity> fileSystemEntities;

    private ScrubbedInput distributionPercentPatrons;
    private ScrubbedInput distributionPercentNonPatrons;

    private ScrubbedInput reason;

    @SuppressWarnings ("unused")
    private CouponCampaign() {
    }

    public CouponCampaign(String campaignId) {
        this.campaignId = new ScrubbedInput(campaignId);
    }

    public String getCampaignId() {
        return campaignId.getText();
    }

    public CouponCampaign setCampaignId(String campaignId) {
        this.campaignId = new ScrubbedInput(campaignId);
        return this;
    }

    public String getRid() {
        return rid.getText();
    }

    public CouponCampaign setRid(String rid) {
        this.rid = new ScrubbedInput(rid);
        return this;
    }

    public String getBizId() {
        return bizId.getText();
    }

    public CouponCampaign setBizId(String bizId) {
        this.bizId = new ScrubbedInput(bizId);
        return this;
    }

    public String getBusinessName() {
        return businessName.getText();
    }

    public CouponCampaign setBusinessName(String businessName) {
        this.businessName = new ScrubbedInput(businessName);
        return this;
    }

    public String getFreeText() {
        return null == freeText ? "" : freeText.getText();
    }

    public CouponCampaign setFreeText(String freeText) {
        this.freeText = new ScrubbedInput(freeText);
        return this;
    }

    public String getAdditionalInfo() {
        return null == additionalInfo ? "" : additionalInfo.getText();
    }

    public CouponCampaign setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = new ScrubbedInput(additionalInfo);
        return this;
    }

    public String getStart() {
        return start.getText();
    }

    public CouponCampaign setStart(String start) {
        this.start = new ScrubbedInput(start);
        return this;
    }

    public String getEnd() {
        return end.getText();
    }

    public CouponCampaign setEnd(String end) {
        this.end = new ScrubbedInput(end);
        return this;
    }

    public String getLive() {
        return live.getText();
    }

    public CouponCampaign setLive(String live) {
        this.live = new ScrubbedInput(live);
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
        return DateUtil.getDaysBetween(start.getText(), end.getText());
    }

    public String getDistributionPercentPatrons() {
        return null == distributionPercentPatrons ? "" : distributionPercentNonPatrons.getText();
    }

    public CouponCampaign setDistributionPercentPatrons(String distributionPercentPatrons) {
        this.distributionPercentPatrons = new ScrubbedInput(distributionPercentPatrons);
        return this;
    }

    @SuppressWarnings ("unused")
    public int getDistributionPercentPatronsAsInt() {
        if (null != distributionPercentPatrons && StringUtils.isNotBlank(distributionPercentPatrons.getText())) {
            return Integer.parseInt(distributionPercentPatrons.getText().substring(0, distributionPercentPatrons.getText().length() - 1));
        } else {
            return 0;
        }
    }

    public String getDistributionPercentNonPatrons() {
        return null == distributionPercentNonPatrons ? "" : distributionPercentNonPatrons.getText();
    }

    public CouponCampaign setDistributionPercentNonPatrons(String distributionPercentNonPatrons) {
        this.distributionPercentNonPatrons = new ScrubbedInput(distributionPercentNonPatrons);
        return this;
    }

    @SuppressWarnings ("unused")
    public int getDistributionPercentNonPatronsAsInt() {
        if (null != distributionPercentNonPatrons && StringUtils.isNotBlank(distributionPercentNonPatrons.getText())) {
            return Integer.parseInt(distributionPercentNonPatrons.getText().substring(0, distributionPercentNonPatrons.getText().length() - 1));
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
        return reason == null ? "" : reason.getText();
    }

    public void setReason(String reason) {
        this.reason = new ScrubbedInput(reason);
    }

    @Transient
    public Map<String, CampaignStatsEntity> getCampaignStats() {
        Map<String, CampaignStatsEntity> campaignStats = new LinkedHashMap<>();
        campaignStats.put(CampaignTypeEnum.P.getName(), new CampaignStatsEntity(getDistributionPercentPatronsAsInt()));
        campaignStats.put(CampaignTypeEnum.NP.getName(), new CampaignStatsEntity(getDistributionPercentNonPatronsAsInt()));
        return campaignStats;
    }
}
