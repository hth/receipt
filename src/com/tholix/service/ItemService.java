package com.tholix.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.repository.ExpenseTypeManager;
import com.tholix.repository.ItemManager;
import com.tholix.utils.Maths;

/**
 * User: hitender
 * Date: 5/19/13
 * Time: 5:07 PM
 */
@Service
public class ItemService {

    @Autowired private ItemManager itemManager;
    @Autowired private ExpenseTypeManager expenseTypeManager;

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

    /**
     * Calculate percentage values of the Expense Items
     *
     * @param profileId
     * @return
     */
    public Map<String, BigDecimal> getAllItemExpense(String profileId) {
        Map<String, BigDecimal> expenseItems = new HashMap<>();
        BigDecimal netSum = BigDecimal.ZERO;

        //Find sum of all items for particular expense
        List<ExpenseTypeEntity> expenseTypeEntities = expenseTypeManager.activeExpenseTypes(profileId);
        for(ExpenseTypeEntity expenseTypeEntity : expenseTypeEntities) {

            BigDecimal sum = BigDecimal.ZERO;
            //Todo this query take a long time. Optimize it. Almost 150ms through this loop
            List<ItemEntity> items = itemManager.getItemEntitiesForSpecificExpenseType(expenseTypeEntity);
            sum = calculateSum(sum, items);
            netSum = Maths.add(netSum, sum);
            expenseItems.put(expenseTypeEntity.getExpName(), sum);
        }

        netSum = populateWithUnAssignedItems(expenseItems, netSum, profileId);

        // Calculate percentage
        for(String key : expenseItems.keySet()) {
            BigDecimal percent = Maths.percent(expenseItems.get(key));
            percent = Maths.divide(percent, netSum);
            expenseItems.put(key, percent);
        }

        return expenseItems;
    }

    /**
     * Calculate sum for all the items
     *
     * @param sum
     * @param items
     * @return
     */
    private BigDecimal calculateSum(BigDecimal sum, List<ItemEntity> items) {
        for(ItemEntity item : items) {
            sum = calculateTotalCost(sum, item);
        }
        return sum;
    }

    /**
     * Finds all the un-assigned items for the user
     *
     * @param expenseItems
     * @param netSum
     * @return
     */
    private BigDecimal populateWithUnAssignedItems(Map<String, BigDecimal> expenseItems, BigDecimal netSum, String profileId) {
        List<ItemEntity> unassignedItems = itemManager.getItemEntitiesForUnAssignedExpenseType(profileId);
        if(unassignedItems.size() > 0) {
            BigDecimal sum = calculateSum(BigDecimal.ZERO, unassignedItems);
            netSum = Maths.add(netSum, sum);
            expenseItems.put("Un-Assigned", sum);
        }
        return netSum;
    }

    /**
     * Calculate total cost of the item with tax. If there are multiple items then multiply with quantity.
     * Helpful in showing the data in donut chart
     *
     * @param sum
     * @param item
     * @return
     */
    public BigDecimal calculateTotalCost(BigDecimal sum, ItemEntity item) {
        sum = Maths.add(sum, item.getTotalPriceWithTax());
        return sum;
    }
}
