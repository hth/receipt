package com.receiptofi.service.analytic;

import com.receiptofi.domain.analytic.ExpensePerUserPerBizEntity;
import com.receiptofi.repository.analytic.ExpensePerUserPerBizManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 6/3/16 7:56 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ExpensePerUserPerBizService {
    private ExpensePerUserPerBizManager expensePerUserPerBizManager;

    @Autowired
    public ExpensePerUserPerBizService(ExpensePerUserPerBizManager expensePerUserPerBizManager) {
        this.expensePerUserPerBizManager = expensePerUserPerBizManager;
    }

    /**
     * Computes total purchase made by customers in a specific business.
     *
     * @param bizId
     * @return
     */
    public ExpensePerUserPerBizEntity getTotalCustomerPurchases(String bizId) {
        List<ExpensePerUserPerBizEntity> expensePerUser = expensePerUserPerBizManager.getTotalCustomerPurchases(bizId);
        if (!expensePerUser.isEmpty()) {
            return expensePerUser.get(0);
        }

        return null;
    }
}
