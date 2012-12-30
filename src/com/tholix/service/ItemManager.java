/**
 * 
 */
package com.tholix.service;

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
	
	public WriteResult updateObject(ItemEntity object);
}
