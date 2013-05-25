package com.tholix.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.types.TaxEnum;
import com.tholix.repository.ExpenseTypeManager;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ReceiptManager;

/**
 * User: hitender
 * Date: 5/19/13
 * Time: 5:07 PM
 */
@Service
public class ItemService {

    @Autowired private ItemManager itemManager;
    @Autowired private ReceiptManager receiptManager;
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
        BigDecimal netSum = new BigDecimal("0.00");

        //Find sum of all items for particular expense
        List<ExpenseTypeEntity> expenseTypeEntities = expenseTypeManager.activeExpenseTypes(profileId);
        for(ExpenseTypeEntity expenseTypeEntity : expenseTypeEntities) {

            BigDecimal sum = new BigDecimal("0.00");
            List<ItemEntity> items = itemManager.getItemEntitiesForSpecificExpenseType(expenseTypeEntity);
            sum = calculateSum(sum, items);
            netSum = netSum.add(sum);
            expenseItems.put(expenseTypeEntity.getExpName(), sum);
        }

        netSum = populateWithUnAssignedItems(expenseItems, netSum, profileId);

        // Calculate percentage
        for(String key : expenseItems.keySet()) {
            BigDecimal percent = (expenseItems.get(key).multiply(new BigDecimal("100.00")).divide(netSum, 2, BigDecimal.ROUND_HALF_UP)).stripTrailingZeros();
            percent = percent.setScale(1, BigDecimal.ROUND_FLOOR);
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
            String receiptId = item.getReceipt().getId();
            ReceiptEntity receiptEntity = receiptManager.findOne(receiptId);
            sum = calculateTotalCost(sum, item, receiptEntity);
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
            BigDecimal sum = calculateSum(new BigDecimal("0.00"), unassignedItems);
            netSum = netSum.add(sum);
            expenseItems.put("Un-Assigned", sum);
        }
        return netSum;
    }

    private BigDecimal calculateTotalCost(BigDecimal sum, ItemEntity item, ReceiptEntity receiptEntity) {
        if(item.getTaxed() == TaxEnum.TAXED) {
            sum = sum.add(new BigDecimal(item.getPrice().toString()).multiply(receiptEntity.getTaxInPercentage()));
        } else {
            sum = sum.add(new BigDecimal(item.getPrice().toString()));
        }
        return sum;
    }
}
