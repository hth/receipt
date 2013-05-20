package com.tholix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.repository.ItemManager;

/**
 * User: hitender
 * Date: 5/19/13
 * Time: 5:07 PM
 */
@Service
public class ItemService {

    @Autowired private ItemManager itemManager;

    public long countItemsUsingExpenseType(ExpenseTypeEntity expenseType) {
        return itemManager.countItemsUsingExpenseType(expenseType.getId());
    }

    public long countItemsUsingExpenseType(String expenseTypeId) {
        return itemManager.countItemsUsingExpenseType(expenseTypeId);
    }
}
