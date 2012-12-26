/**
 * 
 */
package com.tholix.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptUserEntity;

/**
 * @author hitender 
 * @when Dec 16, 2012 1:20:31 PM
 */
public interface ReceiptUserManager extends RepositoryManager<ReceiptUserEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptUserEntity.class, Document.class, "collection");
	
}
