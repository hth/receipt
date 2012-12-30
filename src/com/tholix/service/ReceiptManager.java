/**
 * 
 */
package com.tholix.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 3:09:48 PM
 * 
 */
public interface ReceiptManager extends RepositoryManager<ReceiptEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntity.class, Document.class, "collection");
}
