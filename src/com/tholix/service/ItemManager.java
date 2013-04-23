/**
 *
 */
package com.tholix.service;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 3:10:11 PM
 *
 */
public interface ItemManager extends RepositoryManager<ItemEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(ItemEntity.class, Document.class, "collection");

	public void saveObjects(List<ItemEntity> objects) throws Exception;

	public WriteResult updateObject(ItemEntity object);

	public List<ItemEntity> getWhereReceipt(ReceiptEntity receipt);

	public List<ItemEntity> getAllObjectWithName(String name);

	public void deleteWhereReceipt(ReceiptEntity receipt);

    public List<String> findItems(String name, String bizName);
}
