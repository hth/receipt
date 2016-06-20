package com.receiptofi.domain;

import com.receiptofi.domain.types.BusinessCampaignStatusEnum;
import com.receiptofi.domain.types.BusinessCampaignTypeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 6/8/16 10:23 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BUSINESS_CAMPAIGN")
@CompoundIndexes (value = {
        @CompoundIndex (name = "business_campaign_idx", def = "{'BID': -1}", unique = false),
})
public class BusinessCampaignEntity extends BaseEntity {

    @Field ("BID")
    private String bizId;

    @Field ("RID")
    private String rid;

    @Field ("FT")
    private String freeText;

    @Field ("SP")
    private Date start;

    @Field ("EP")
    private Date end;

    @Field ("LP")
    private Date live;

    @Field ("BT")
    private BusinessCampaignTypeEnum businessCampaignType = BusinessCampaignTypeEnum.L;

    /** Number of coupon to release. */
    @Field ("DP")
    private int distributionPercent;

    /** Number of coupon distributed. Computed by system. */
    @Field ("TD")
    private int totalDistribution;

    @DBRef
    @Field ("FS")
    private Collection<FileSystemEntity> fileSystemEntities;

    @NotNull
    @Field ("CS")
    private BusinessCampaignStatusEnum businessCampaignStatus = BusinessCampaignStatusEnum.I;

    @DBRef
    @Field ("AI")
    private CommentEntity additionalInfo;

    @SuppressWarnings("unused")
    private BusinessCampaignEntity() {
        super();
    }

    private BusinessCampaignEntity(String rid, String bizId, String freeText, Date start, Date end, Date live) {
        super();
        this.rid = rid;
        this.bizId = bizId;
        this.freeText = freeText;
        this.start = start;
        this.end = end;
        this.live = live;
    }

    public static BusinessCampaignEntity newInstance(String rid, String bizId, String freeText, Date start, Date end, Date live) {
        return new BusinessCampaignEntity(rid, bizId, freeText, start, end, live);
    }

    public String getBizId() {
        return bizId;
    }

    public BusinessCampaignEntity setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getRid() {
        return rid;
    }

    public BusinessCampaignEntity setRid(String rid) {
        this.rid = rid;
        return this;
    }

    public String getFreeText() {
        return freeText;
    }

    public BusinessCampaignEntity setFreeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public BusinessCampaignEntity setStart(Date start) {
        this.start = start;
        return this;
    }

    public Date getEnd() {
        return end;
    }

    public BusinessCampaignEntity setEnd(Date end) {
        this.end = end;
        return this;
    }

    public Date getLive() {
        return live;
    }

    public BusinessCampaignEntity setLive(Date live) {
        this.live = live;
        return this;
    }

    public BusinessCampaignTypeEnum getBusinessPromoType() {
        return businessCampaignType;
    }

    public BusinessCampaignEntity setBusinessPromoType(BusinessCampaignTypeEnum businessCampaignType) {
        this.businessCampaignType = businessCampaignType;
        return this;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public BusinessCampaignEntity setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
        return this;
    }

    public int getDistributionPercent() {
        return distributionPercent;
    }

    public BusinessCampaignEntity setDistributionPercent(int distributionPercent) {
        this.distributionPercent = distributionPercent;
        return this;
    }

    public int getTotalDistribution() {
        return totalDistribution;
    }

    public BusinessCampaignEntity setTotalDistribution(int totalDistribution) {
        this.totalDistribution = totalDistribution;
        return this;
    }

    public BusinessCampaignStatusEnum getBusinessCampaignStatus() {
        return businessCampaignStatus;
    }

    public BusinessCampaignEntity setBusinessCampaignStatus(BusinessCampaignStatusEnum businessCampaignStatus) {
        this.businessCampaignStatus = businessCampaignStatus;
        return this;
    }

    public CommentEntity getAdditionalInfo() {
        return additionalInfo;
    }

    public BusinessCampaignEntity setAdditionalInfo(CommentEntity additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }
}