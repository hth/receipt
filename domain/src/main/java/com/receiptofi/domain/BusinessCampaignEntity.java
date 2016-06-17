package com.receiptofi.domain;

import com.receiptofi.domain.types.BusinessCampaignTypeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.Date;

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
        @CompoundIndex (name = "business_campaign_idx", def = "{'BID': -1}", unique = true),
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

    /** Number of coupon distributed. */
    @Field ("TD")
    private int totalDistribution;

    @DBRef
    @Field ("FS")
    private Collection<FileSystemEntity> fileSystemEntities;

    @SuppressWarnings("unused")
    private BusinessCampaignEntity() {
        super();
    }

    private BusinessCampaignEntity(String bizId, String rid, Date start, Date end, Date live) {
        super();
        this.bizId = bizId;
        this.rid = rid;
        this.start = start;
        this.end = end;
        this.live = live;
    }

    public static BusinessCampaignEntity newInstance(String bizId, String rid, Date start, Date end, Date live) {
        return new BusinessCampaignEntity(bizId, rid, start, end, live);
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getLive() {
        return live;
    }

    public void setLive(Date live) {
        this.live = live;
    }

    public BusinessCampaignTypeEnum getBusinessPromoType() {
        return businessCampaignType;
    }

    public void setBusinessPromoType(BusinessCampaignTypeEnum businessCampaignType) {
        this.businessCampaignType = businessCampaignType;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public void setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
    }

    public int getDistributionPercent() {
        return distributionPercent;
    }

    public void setDistributionPercent(int distributionPercent) {
        this.distributionPercent = distributionPercent;
    }

    public int getTotalDistribution() {
        return totalDistribution;
    }

    public void setTotalDistribution(int totalDistribution) {
        this.totalDistribution = totalDistribution;
    }
}
