/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptEntityOCR;

/**
 * @author hitender
 * @when Jan 6, 2013 1:29:22 PM
 * 
 */
public interface ReceiptOCRManager extends RepositoryManager<ReceiptEntityOCR> {

	public static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntityOCR.class, Document.class, "collection");

	public long numberOfPendingReceipts(String userProfileId);
	
	public List<ReceiptEntityOCR> getAllObjects(String userProfileId);
}
