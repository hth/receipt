package com.receiptofi.service;

import com.mongodb.client.DistinctIterable;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.repository.CronStatsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 4/21/15 1:26 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CronStatsService {
    private CronStatsManager cronStatsManager;

    @Autowired
    public CronStatsService(CronStatsManager cronStatsManager) {
        this.cronStatsManager = cronStatsManager;
    }

    public void save(CronStatsEntity cronStats) {
        cronStats.setEnd(new Date());
        cronStatsManager.save(cronStats);
    }

    public Map<String, List<CronStatsEntity>> getUniqueCronTasks(int limit) {
        Map<String, List<CronStatsEntity>> taskStats = new LinkedHashMap<>();
        DistinctIterable<String> tasks = cronStatsManager.getUniqueCronTasks();
        for (String task : tasks) {
            taskStats.put(task, cronStatsManager.getHistoricalData(task, limit));
        }
        return taskStats;
    }
}
