/**
 *
 */
package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;
import org.joda.time.DateTime;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @since Dec 26, 2012 3:10:11 PM
 *
 */
public interface ItemManager extends RepositoryManager<ItemEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(ItemEntity.class, Document.class, "collection");

	void saveObjects(List<ItemEntity> objects) throws Exception;

	WriteResult updateObject(ItemEntity object);

	List<ItemEntity> getWhereReceipt(ReceiptEntity receipt);

    /**
     * Gets items with specified name until the specified date
     *
     * @param name
     * @param untilThisDay
     * @return
     */
	List<ItemEntity> findAllByNameLimitByDays(String name, DateTime untilThisDay);

    /**
     * Should be used only for listing historical data of the items for a particular user
     *
     * @param itemEntity
     * @param userProfileId
     * @return
     */
    List<ItemEntity> findAllByName(ItemEntity itemEntity, String userProfileId);

	void deleteWhereReceipt(ReceiptEntity receipt);

    /**
     * Populate with just 'name' field based on first token. Used in AJAX call.
     *
     * @param name
     * @param bizName
     * @return
     */
    List<ItemEntity> findItems(String name, String bizName);

    /**
     * Get the Item from DB and then update with changed ExpenseType before persisting Item
     *
     * @param item
     */
    void updateItemWithExpenseType(ItemEntity item) throws Exception;

    /**
     * Count how many Items are using a particular Expense Type
     *
     * @param expenseTypeId
     * @return
     */
    long countItemsUsingExpenseType(String expenseTypeId);

    long collectionSize();

    List<ItemEntity> getItemEntitiesForSpecificExpenseType(ExpenseTypeEntity expenseType);

    List<ItemEntity> getItemEntitiesForUnAssignedExpenseType(String userProfileId);
}