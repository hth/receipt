/**
 *
 */
package com.tholix.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

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

	List<ItemEntity> getAllObjectWithName(String name);

	void deleteWhereReceipt(ReceiptEntity receipt);

    /**
     * Populate with just 'name' field based on first token. Used in AJAX call.
     *
     * @param name
     * @param bizName
     * @return
     */
    List<ItemEntity> findItems(String name, String bizName);

    void updateItemExpenseType(ItemEntity item);

    /**
     * Count how many Items are using a particular Expense Type
     *
     * @param expenseTypeId
     * @return
     */
    long countItemsUsingExpenseType(String expenseTypeId);

    long collectionSize();

    /**
     * Calculate percentage values of the Expense Items
     *
     * @param profileId
     * @return
     */
    Map<String, BigDecimal> getAllItemExpense(String profileId);

    List<ItemEntity> getItemEntitiesForSpecificExpenseType(ExpenseTypeEntity expenseTypeEntity);

    List<ItemEntity> getItemEntitiesForSpecificExpenseType(String expenseTypeId);

    List<ItemEntity> getItemEntitiesForUnAssignedExpenseType(String userProfileId);
}
