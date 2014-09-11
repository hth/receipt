package com.receiptofi.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.repository.ExpenseTagManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.utils.Maths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static java.math.BigDecimal.ZERO;

/**
 * User: hitender
 * Date: 5/19/13
 * Time: 5:07 PM
 */
@Service
public final class ItemService {

    @Autowired private ItemManager itemManager;
    @Autowired private ExpenseTagManager expenseTagManager;

    public long countItemsUsingExpenseType(String expenseTypeId, String userProfileId) {
        return itemManager.countItemsUsingExpenseType(expenseTypeId, userProfileId);
    }

    public List<ItemEntity> itemsForExpenseType(ExpenseTagEntity expenseTagEntity) {
        return itemManager.getItemEntitiesForSpecificExpenseTypeForTheYear(expenseTagEntity);
    }

    public List<ItemEntity> itemsForUnAssignedExpenseType(String userProfileId) {
        return itemManager.getItemEntitiesForUnAssignedExpenseTypeForTheYear(userProfileId);
    }

    public List<ItemEntity> getAllItemsOfReceipt(String receiptId) {
        return itemManager.getAllItemsOfReceipt(receiptId);
    }

    /**
     * Calculate percentage values of the Expense Items
     *
     * @param profileId
     * @return
     */
    public Map<String, BigDecimal> getAllItemExpenseForTheYear(String profileId) {
        Map<String, BigDecimal> expenseItems = new HashMap<>();
        BigDecimal netSum = ZERO;

        //Find sum of all items for particular expense
        List<ExpenseTagEntity> expenseTypeEntities = expenseTagManager.activeExpenseTypes(profileId);
        for(ExpenseTagEntity expenseTagEntity : expenseTypeEntities) {

            BigDecimal sum = ZERO;
            //Todo this query take a long time. Optimize it. Almost 150ms through this loop
            List<ItemEntity> items = itemManager.getItemEntitiesForSpecificExpenseTypeForTheYear(expenseTagEntity);
            sum = calculateSum(sum, items);
            netSum = Maths.add(netSum, sum);
            expenseItems.put(expenseTagEntity.getTagName(), sum);
        }

        netSum = populateWithUnAssignedItems(expenseItems, netSum, profileId);

        // Calculate percentage
        for(String key : expenseItems.keySet()) {
            BigDecimal percent = Maths.percent(expenseItems.get(key));
            expenseItems.put(key, (netSum == ZERO) ? ZERO : Maths.divide(percent, netSum));
            //percent = Maths.divide(percent, netSum);
            //expenseItems.put(key, percent);
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
        Assert.notNull(sum);
        BigDecimal newSum = sum;
        for(ItemEntity item : items) {
            newSum = calculateTotalCost(newSum, item);
        }
        return newSum;
    }

    /**
     * Finds all the un-assigned items for the user
     * @param expenseItems
     * @param netSum
     * @param profileId
     * @return
     */
    private BigDecimal populateWithUnAssignedItems(Map<String, BigDecimal> expenseItems, BigDecimal netSum, String profileId) {
        List<ItemEntity> unassignedItems = itemManager.getItemEntitiesForUnAssignedExpenseTypeForTheYear(profileId);
        Assert.notNull(netSum);
        BigDecimal newNetSum = netSum;
        if(unassignedItems.isEmpty()) {
            BigDecimal sum = calculateSum(ZERO, unassignedItems);
            newNetSum = Maths.add(newNetSum, sum);
            expenseItems.put("Un-Assigned", sum);
        }
        return newNetSum;
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
        Assert.notNull(sum);
        return Maths.add(sum, item.getTotalPriceWithTax());
    }
}
