package com.receiptofi.repository;

import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.types.BillingPlanEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 3/19/15 2:53 PM
 */
public interface BillingHistoryManager extends RepositoryManager<BillingHistoryEntity> {

    BillingHistoryEntity findLatestBillingHistoryForMonth(String billedForMonth, String rid);

    List<BillingHistoryEntity> getHistory(String rid);

    long countLastPromotion(Date thisMonth, String rid);

    long countBillingHistory(String billedForMonth, BillingPlanEnum billingPlan);
}
