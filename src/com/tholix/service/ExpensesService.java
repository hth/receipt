package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.repository.ExpenseTypeManager;

/**
 * User: hitender
 * Date: 5/23/13
 * Time: 11:49 PM
 */
@Service
public class ExpensesService {

    @Autowired private ExpenseTypeManager expenseTypeManager;

    public List<ExpenseTypeEntity> activeExpenseTypes(String userProfileId) {
        return expenseTypeManager.activeExpenseTypes(userProfileId);
    }
}
