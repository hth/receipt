/**
 *
 */
package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

import com.tholix.domain.BaseEntity;
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

    List<String> findItems(String name, String bizName);

    void updateItemExpenseType(ItemEntity item);

    long countItemsUsingExpenseType(String expenseTypeId);

    long collectionSize();
}
