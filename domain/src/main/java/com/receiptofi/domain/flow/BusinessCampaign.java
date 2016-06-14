package com.receiptofi.domain.flow;

import static com.receiptofi.utils.DateUtil.DF_MMDDYYYY;

import org.joda.time.Interval;
import org.joda.time.PeriodType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.text.ParseException;

/**
 * User: hitender
 * Date: 6/10/16 12:04 PM
 */
public class BusinessCampaign implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCampaign.class);

    private String rid;
    private String bizId;
    private String businessName;
    private String freeText;
    private String start;
    private String end;
    private String live;

    public String getRid() {
        return rid;
    }

    public BusinessCampaign setRid(String rid) {
        this.rid = rid;
        return this;
    }

    public String getBizId() {
        return bizId;
    }

    public BusinessCampaign setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BusinessCampaign setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getFreeText() {
        return freeText;
    }

    public BusinessCampaign setFreeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public String getStart() {
        return start;
    }

    public BusinessCampaign setStart(String start) {
        this.start = start;
        return this;
    }

    public String getEnd() {
        return end;
    }

    public BusinessCampaign setEnd(String end) {
        this.end = end;
        return this;
    }

    public String getLive() {
        return live;
    }

    public BusinessCampaign setLive(String live) {
        this.live = live;
        return this;
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @return
     */
    @SuppressWarnings ("unused")
    public int getDaysBetween() {
        try {
            Assert.notNull(start);
            Assert.notNull(end);
            Interval interval = new Interval(DF_MMDDYYYY.parse(start).getTime(), DF_MMDDYYYY.parse(end).getTime());
            return interval.toPeriod(PeriodType.days()).getDays();
        } catch (ParseException e) {
            LOG.warn("Failed to parse date start={} end={}", start, end);
            return -1;
        }
    }
}
