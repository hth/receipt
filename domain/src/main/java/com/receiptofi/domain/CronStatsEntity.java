package com.receiptofi.domain;

import com.receiptofi.utils.DateUtil;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 4/21/15 12:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "CRON_STATS")
@CompoundIndexes (value = {
        @CompoundIndex (name = "cron_stats_idx", def = "{'C': -1}", background = true)
})
public class CronStatsEntity extends BaseEntity {

    @Field ("CN")
    private String className;

    @Field ("TN")
    private String taskName;

    @Field ("PS")
    private String processStatus;

    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE_TIME)
    @Field ("EN")
    private Date end;

    @Field ("ST")
    private Map<String, String> stats = new LinkedHashMap<>();

    public CronStatsEntity(String className, String taskName, String processStatus) {
        this.className = className;
        this.taskName = taskName;
        this.processStatus = processStatus;
    }

    public String getClassName() {
        return className;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public Map<String, String> getStats() {
        return stats;
    }

    public void setStats(Map<String, String> stats) {
        this.stats = stats;
    }

    public void addStats(String key, String value) {
        this.stats.put(key, value);
    }

    public void addStats(String key, int value) {
        this.stats.put(key, String.valueOf(value));
    }

    public void addStats(String key, long value) {
        this.stats.put(key, String.valueOf(value));
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Transient
    public long getDuration() {
        return DateUtil.getDuration(getCreated(), end);
    }
}
