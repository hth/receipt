package com.receiptofi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.receiptofi.domain.ExpenseTypeEntity;
import com.receiptofi.repository.ExpenseTypeManager;

/**
 * User: hitender
 * Date: 5/23/13
 * Time: 11:49 PM
 */
@Service
public final class ExpensesService {

    @Autowired private ExpenseTypeManager expenseTypeManager;

    public List<ExpenseTypeEntity> activeExpenseTypes(String userProfileId) {
        return expenseTypeManager.activeExpenseTypes(userProfileId);
    }
}
