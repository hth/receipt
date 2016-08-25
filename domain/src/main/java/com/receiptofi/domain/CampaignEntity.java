package com.receiptofi.domain;

import com.receiptofi.domain.types.CampaignStatusEnum;
import com.receiptofi.utils.DateUtil;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
@Document (collection = "CAMPAIGN")
@CompoundIndexes (value = {
        @CompoundIndex (name = "campaign_idx", def = "{'BID': -1}", unique = false),
})
public class CampaignEntity extends BaseEntity {

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

    @DBRef
    @Field ("FS")
    private Collection<FileSystemEntity> fileSystemEntities;

    @NotNull
    @Field ("CS")
    private CampaignStatusEnum campaignStatus = CampaignStatusEnum.N;

    @NotNull
    @Field ("HS")
    private Map<Date, CampaignStatusEnum> historicalCampaignStates = new LinkedHashMap<>();

    @DBRef
    @Field ("AI")
    private CommentEntity additionalInfo;

    @Field ("ST")
    private Map<String, CampaignStatsEntity> campaignStats;

    @Field ("RS")
    private String reason;

    @SuppressWarnings("unused")
    private CampaignEntity() {
        super();
    }

    private CampaignEntity(String rid, String bizId, String freeText, Date start, Date end, Date live) {
        super();
        this.rid = rid;
        this.bizId = bizId;
        this.freeText = freeText;
        this.start = start;
        this.end = end;
        this.live = live;
        historicalCampaignStates.put(new Date(), campaignStatus);
    }

    public static CampaignEntity newInstance(String rid, String bizId, String freeText, Date start, Date end, Date live) {
        return new CampaignEntity(rid, bizId, freeText, start, end, live);
    }

    public String getBizId() {
        return bizId;
    }

    public CampaignEntity setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getRid() {
        return rid;
    }

    public CampaignEntity setRid(String rid) {
        this.rid = rid;
        return this;
    }

    public String getFreeText() {
        return freeText;
    }

    public CampaignEntity setFreeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public CampaignEntity setStart(Date start) {
        this.start = start;
        return this;
    }

    public Date getEnd() {
        return end;
    }

    public CampaignEntity setEnd(Date end) {
        this.end = end;
        return this;
    }

    public Date getLive() {
        return live;
    }

    public CampaignEntity setLive(Date live) {
        this.live = live;
        return this;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public CampaignEntity setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
        return this;
    }

    public CampaignStatusEnum getCampaignStatus() {
        return campaignStatus;
    }

    public CampaignEntity setCampaignStatus(CampaignStatusEnum campaignStatus) {
        this.campaignStatus = campaignStatus;
        historicalCampaignStates.put(new Date(), campaignStatus);
        return this;
    }

    public Map<Date, CampaignStatusEnum> getHistoricalCampaignStates() {
        return historicalCampaignStates;
    }

    public CommentEntity getAdditionalInfo() {
        return additionalInfo;
    }

    public CampaignEntity setAdditionalInfo(CommentEntity additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public Map<String, CampaignStatsEntity> getCampaignStats() {
        return campaignStats;
    }

    public void setCampaignStats(Map<String, CampaignStatsEntity> campaignStats) {
        this.campaignStats = campaignStats;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Transient
    public int getDaysBetween() {
        return DateUtil.getDaysBetween(start, end);
    }

    @Override
    public String toString() {
        return "CampaignEntity{" +
                "id=" + id +
                ", bizId='" + bizId + '\'' +
                ", rid='" + rid + '\'' +
                ", freeText='" + freeText + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", live=" + live +
                ", fileSystemEntities=" + fileSystemEntities +
                ", campaignStatus=" + campaignStatus +
                ", historicalCampaignStates=" + historicalCampaignStates +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
