package com.receiptofi.repository.analytic;

import com.receiptofi.domain.analytic.ExpensePerUserPerBizEntity;
import com.receiptofi.repository.RepositoryManager;

import java.util.List;

/**
 * User: hitender
 * Date: 6/3/16 6:20 PM
 */
public interface ExpensePerUserPerBizManager extends RepositoryManager<ExpensePerUserPerBizEntity> {

    List<ExpensePerUserPerBizEntity> getTotalCustomerPurchases(String bizId);
}
