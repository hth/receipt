/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.WriteResult;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;

import org.joda.time.DateTime;

import java.util.List;

/**
 * @author hitender
 * @since Dec 26, 2012 3:10:11 PM
 */
public interface ItemManager extends RepositoryManager<ItemEntity> {

    void saveObjects(List<ItemEntity> objects);

    WriteResult updateObject(ItemEntity object);

    List<ItemEntity> getAllItemsOfReceipt(String receiptId);

    /**
     * Finds users item as the session supplies receiptUserId.
     *
     * @param itemId
     * @param receiptUserId
     * @return
     */
    ItemEntity findItem(String itemId, String receiptUserId);

    /**
     * Gets items with specified name until the specified date.
     *
     * @param name         - Name of the item
     * @param untilThisDay - Show result from this day onwards
     * @return
     */
    List<ItemEntity> findAllByNameLimitByDays(String name, DateTime untilThisDay);

    /**
     * Gets items with specified name until the specified date.
     *
     * @param name          - Name of the item
     * @param receiptUserId
     * @param untilThisDay  - Show result from this day onwards
     * @return
     */
    List<ItemEntity> findAllByNameLimitByDays(String name, String receiptUserId, DateTime untilThisDay);


    /**
     * Should be used only for listing historical data of the items for a particular user.
     *
     * @param item
     * @param receiptUserId
     * @return
     */
    List<ItemEntity> findAllByName(ItemEntity item, String receiptUserId, int limit);

    /**
     * Gets count of the item with same name purchased.
     *
     * @param item
     * @param receiptUserId
     * @return
     */
    long findAllByNameCount(ItemEntity item, String receiptUserId);

    /**
     * Delete Entity.
     *
     * @param receipt
     */
    void deleteWhereReceipt(ReceiptEntity receipt);

    /**
     * Marks an entity deleted but does not delete it.
     *
     * @param receipt
     */
    void deleteSoft(ReceiptEntity receipt);

    /**
     * Populate with just 'name' field based on first token. Used in AJAX call.
     *
     * @param name
     * @param bizName
     * @return
     */
    List<ItemEntity> findItems(String name, String bizName);

    void updateAllItemWithExpenseTag(String receiptId, String expenseTagId);

    void updateItemWithExpenseTag(String itemId, String expenseTagId);

    /**
     * Count how many Items are using a particular Expense Type.
     *
     * @param expenseTypeId
     * @param receiptUserId
     * @return
     */
    long countItemsUsingExpenseType(String expenseTypeId, String receiptUserId);

    List<ItemEntity> getItemEntitiesForSpecificExpenseTypeForTheYear(ExpenseTagEntity expenseType);

    List<ItemEntity> getItemEntitiesForUnAssignedExpenseTypeForTheYear(String receiptUserId);

    /**
     * Collection size.
     */
    long collectionSize();

    List<ItemEntity> getAll();
}
