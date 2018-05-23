/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.client.result.UpdateResult;
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

    UpdateResult updateObject(ItemEntity object);

    List<ItemEntity> getAllItemsOfReceipt(String receiptId);

    /**
     * Find Item for RID with Item Id.
     *
     * @param itemId
     * @param rid
     * @return
     */
    ItemEntity findItem(String itemId, String rid);

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
     * @param rid
     * @return
     */
    List<ItemEntity> findAllByName(ItemEntity item, String rid, int limit);

    /**
     * Gets count of the item with same name purchased.
     *
     * @param item
     * @param rid
     * @return
     */
    long findAllByNameCount(ItemEntity item, String rid);

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
     * @param rid
     * @return
     */
    long countItemsUsingExpenseType(String expenseTypeId, String rid);

    List<ItemEntity> getItemEntitiesForSpecificExpenseTypeForTheYear(ExpenseTagEntity expenseType);

    List<ItemEntity> getItemEntitiesForUnAssignedExpenseTypeForTheYear(String rid);

    /**
     * Remove reference to expense tag.
     *
     * @param rid
     * @param expenseTagId
     * @return
     */
    boolean removeExpenseTagReferences(String rid, String expenseTagId);

    /**
     * Collection size.
     */
    long collectionSize();
}
