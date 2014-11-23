package com.receiptofi.repository;

import com.receiptofi.domain.DocumentDailyStatEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 11/20/14 2:45 PM
 */
public interface DocumentDailyStatManager extends RepositoryManager<DocumentDailyStatEntity> {
    List<DocumentDailyStatEntity> getStatsForDays(int days);

    /**
     * Returns latest record entered.
     * @return
     */
    DocumentDailyStatEntity getLastEntry();
}
