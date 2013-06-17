/**
 *
 */
package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptEntityOCR;

/**
 * @author hitender
 * @since Jan 6, 2013 1:29:22 PM
 *
 */
public interface ReceiptOCRManager extends RepositoryManager<ReceiptEntityOCR> {

	static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntityOCR.class, Document.class, "collection");

	long numberOfPendingReceipts(String userProfileId);

    /**
     * Get all the pending receipts
     *
     * @param userProfileId
     * @return
     */
	List<ReceiptEntityOCR> getAllObjects(String userProfileId);
}
