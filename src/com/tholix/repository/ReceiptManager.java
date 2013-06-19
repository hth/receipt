/**
 *
 */
package com.tholix.repository;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.value.ReceiptGrouped;

/**
 * @author hitender
 * @since Dec 26, 2012 3:09:48 PM
 *
 */
public interface ReceiptManager extends RepositoryManager<ReceiptEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntity.class, Document.class, "collection");

    /**
     * Gets all the user receipts
     *
     * @param userProfileId
     * @return
     */
	List<ReceiptEntity> getAllReceipts(String userProfileId);

    /**
     * Gets user receipts for current month
     *
     * @param userProfileId
     * @return
     */
    List<ReceiptEntity> getAllReceiptsForThisMonth(String userProfileId);

    /**
     * Get receipts associated with year, month, day
     *
     * @param year
     * @param month
     * @param day
     * @param userProfileId
     * @return
     */
    List<ReceiptEntity> findThisDayReceipts(int year, int month, int day, String userProfileId);

    Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String userProfileId);

    List<String> findTitles(String title);

    ReceiptEntity findWithReceiptOCR(String receiptOCRId);
}
