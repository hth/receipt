package com.receiptofi.repository;

import com.receiptofi.domain.CronStatsEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 4/21/15 12:39 PM
 */
public interface CronStatsManager extends RepositoryManager<CronStatsEntity> {

    List<String> getUniqueCronTasks();

    List<CronStatsEntity> getHistoricalData(String task, int limit);
}
