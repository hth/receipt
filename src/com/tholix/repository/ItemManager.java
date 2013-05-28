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
     * Only way to append a expense type object to Item otherwise it only appends expense type Id
     * @param item
     */
    void appendExpenseType(ItemEntity item);

    /**
     * Count how many Items are using a particular Expense Type
     *
     * @param expenseTypeId
     * @return
     */
    long countItemsUsingExpenseType(String expenseTypeId);

    long collectionSize();

    List<ItemEntity> getItemEntitiesForSpecificExpenseType(ExpenseTypeEntity expenseTypeEntity);

    List<ItemEntity> getItemEntitiesForSpecificExpenseType(String expenseTypeId);

    List<ItemEntity> getItemEntitiesForUnAssignedExpenseType(String userProfileId);
}