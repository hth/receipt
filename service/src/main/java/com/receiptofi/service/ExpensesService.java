package com.receiptofi.service;

import java.util.List;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.repository.ExpenseTagManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 5/23/13
 * Time: 11:49 PM
 */
@Service
public final class ExpensesService {

    @Autowired private ExpenseTagManager expenseTagManager;

    public List<ExpenseTagEntity> activeExpenseTypes(String userProfileId) {
        return expenseTagManager.activeExpenseTypes(userProfileId);
    }
}
