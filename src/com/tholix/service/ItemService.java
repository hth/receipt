package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
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

    public List<ItemEntity> itemsForExpenseType(ExpenseTypeEntity expenseTypeEntity) {
        return itemManager.getItemEntitiesForSpecificExpenseType(expenseTypeEntity);
    }

    public List<ItemEntity> itemsForUnAssignedExpenseType(String userProfileId) {
        return itemManager.getItemEntitiesForUnAssignedExpenseType(userProfileId);
    }
}
