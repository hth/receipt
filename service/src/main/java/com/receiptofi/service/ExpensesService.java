package com.receiptofi.service;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.repository.ExpenseTagManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 5/23/13
 * Time: 11:49 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public final class ExpensesService {

    @Autowired private ExpenseTagManager expenseTagManager;

    public List<ExpenseTagEntity> activeExpenseTypes(String userProfileId) {
        return expenseTagManager.activeExpenseTypes(userProfileId);
    }
}
