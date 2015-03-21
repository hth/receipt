package com.receiptofi.repository;

import com.receiptofi.domain.BillingHistoryEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 3/19/15 2:53 PM
 */
public interface BillingHistoryManager extends RepositoryManager<BillingHistoryEntity> {

    BillingHistoryEntity findBillingHistoryForMonth(String billedForMonth, String rid);

    List<BillingHistoryEntity> getHistory(String rid);
}
