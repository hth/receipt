/**
 *
 */
package com.tholix.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * @author hitender
 * @since Dec 26, 2012 3:09:48 PM
 *
 */
public interface ReceiptManager extends RepositoryManager<ReceiptEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntity.class, Document.class, "collection");

	List<ReceiptEntity> getAllObjectsForUser(String userProfileId);

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

	//public List<ReceiptGrouped> getAllObjectsGroupedByDate(String userProfileId);
	Map<Date, BigDecimal> getAllObjectsGroupedByDate(String userProfileId);

    List<String> findTitles(String title);

    long collectionSize();
}
