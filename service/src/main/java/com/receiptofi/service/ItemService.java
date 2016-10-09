package com.receiptofi.service;

import static java.math.BigDecimal.ZERO;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.ExpenseTagIconEnum;
import com.receiptofi.repository.ExpenseTagManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.service.wrapper.ThisYearExpenseByTag;
import com.receiptofi.utils.Maths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 5/19/13
 * Time: 5:07 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ItemService {

    private ItemManager itemManager;
    private ExpenseTagManager expenseTagManager;

    @Autowired
    public ItemService(ItemManager itemManager, ExpenseTagManager expenseTagManager) {
        this.itemManager = itemManager;
        this.expenseTagManager = expenseTagManager;
    }

    public void updateAllItemWithExpenseTag(String receiptId, String expenseTagId) {
        itemManager.updateAllItemWithExpenseTag(receiptId, expenseTagId);
    }

    public void updateItemWithExpenseTag(String itemId, String expenseTagId) {
        itemManager.updateItemWithExpenseTag(itemId, expenseTagId);
    }

    public ItemEntity findItem(String itemId, String rid) {
        return itemManager.findItem(itemId, rid);
    }

    public long countItemsUsingExpenseType(String expenseTypeId, String rid) {
        return itemManager.countItemsUsingExpenseType(expenseTypeId, rid);
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
     * Calculate percentage values of the Expense Items.
     *
     * @param rid
     * @return
     */
    List<ThisYearExpenseByTag> getAllItemExpenseForTheYear(String rid) {
        List<ThisYearExpenseByTag> thisYearExpenseByTags = new LinkedList<>();
        BigDecimal netSum = ZERO;

        //Find sum of all items for particular expense
        List<ExpenseTagEntity> expenseTags = expenseTagManager.getExpenseTags(rid);
        for (ExpenseTagEntity expenseTag : expenseTags) {

            BigDecimal sum = ZERO;
            //Todo this query take a long time. Optimize it. Almost 150ms through this loop
            List<ItemEntity> items = itemManager.getItemEntitiesForSpecificExpenseTypeForTheYear(expenseTag);
            sum = calculateSum(sum, items);
            netSum = Maths.add(netSum, sum);
            thisYearExpenseByTags.add(new ThisYearExpenseByTag(expenseTag.getTagName(), expenseTag.getTagColor(), expenseTag.getIcon().getWebLocationWithFilename(), sum));
        }

        netSum = populateWithUnAssignedItems(thisYearExpenseByTags, netSum, rid);

        // Calculate percentage
        for (ThisYearExpenseByTag thisYearExpenseByTag : thisYearExpenseByTags) {
            BigDecimal percent = Maths.percent(thisYearExpenseByTag.getTotal());
            thisYearExpenseByTag.setPercentage((netSum.compareTo(ZERO) == 0) ? ZERO : Maths.divide(percent, netSum));
            //percent = Maths.divide(percent, netSum);
            //expenseItems.put(key, percent);
        }

        return thisYearExpenseByTags;
    }

    /**
     * Calculate sum for all the items.
     *
     * @param sum
     * @param items
     * @return
     */
    private BigDecimal calculateSum(BigDecimal sum, List<ItemEntity> items) {
        Assert.notNull(sum);
        BigDecimal newSum = sum;
        for (ItemEntity item : items) {
            newSum = calculateTotalCost(newSum, item);
        }
        return newSum;
    }

    /**
     * Finds all the un-assigned items for the user.
     *
     * @param thisYearExpenseByTags
     * @param netSum
     * @param rid
     * @return
     */
    private BigDecimal populateWithUnAssignedItems(List<ThisYearExpenseByTag> thisYearExpenseByTags, BigDecimal netSum, String rid) {
        Assert.notNull(netSum);
        BigDecimal newNetSum = netSum;

        List<ItemEntity> unassignedItems = itemManager.getItemEntitiesForUnAssignedExpenseTypeForTheYear(rid);
        if (!unassignedItems.isEmpty()) {
            BigDecimal sum = calculateSum(ZERO, unassignedItems);
            newNetSum = Maths.add(newNetSum, sum);
            thisYearExpenseByTags.add(new ThisYearExpenseByTag("Un-Assigned", "#808080", ExpenseTagIconEnum.V100.getWebLocationWithFilename(), sum));
        }
        return newNetSum;
    }

    /**
     * Calculate total cost of the item with tax. If there are multiple items then multiply with quantity.
     * Helpful in showing the data in donut chart.
     *
     * @param sum
     * @param item
     * @return
     */
    public BigDecimal calculateTotalCost(BigDecimal sum, ItemEntity item) {
        Assert.notNull(sum);
        return Maths.add(sum, item.getTotalPriceWithTax());
    }

    public void deleteWhereReceipt(ReceiptEntity receipt) {
        itemManager.deleteWhereReceipt(receipt);
    }

    public void deleteSoft(ReceiptEntity receipt) {
        itemManager.deleteSoft(receipt);
    }
}
