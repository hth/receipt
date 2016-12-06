package com.receiptofi.web.form;

import com.receiptofi.domain.CronStatsEntity;

import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 4/22/15 2:33 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class CronStatsForm {

    private Map<String, List<CronStatsEntity>> taskStats;

    public Map<String, List<CronStatsEntity>> getTaskStats() {
        return taskStats;
    }

    public void setTaskStats(Map<String, List<CronStatsEntity>> taskStats) {
        this.taskStats = taskStats;
    }
}
