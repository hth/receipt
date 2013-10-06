/**
 *
 */
package com.tholix.repository;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import org.joda.time.DateTime;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.value.ReceiptGrouped;
import com.tholix.domain.value.ReceiptGroupedByBizLocation;

/**
 * @author hitender
 * @since Dec 26, 2012 3:09:48 PM
 *
 */
public interface ReceiptManager extends RepositoryManager<ReceiptEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(ReceiptEntity.class, Document.class, "collection");

    int SHOW_DATA_FOR_LAST_X_MONTHS = 13;

    /**
     *
     * @param receiptId
     * @param userProfileId
     * @return
     */
    ReceiptEntity findReceipt(String receiptId, String userProfileId);

    /**
     * Find all receipts with BizName for the user
     * @param bizNameEntity
     * @param userProfileId
     * @return
     */
    List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String userProfileId);

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
    List<ReceiptEntity> getAllReceiptsForThisMonth(String userProfileId, DateTime monthYear);

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

    /**
     * Receipt grouped by day
     *
     * @param userProfileId
     * @return
     */
    Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String userProfileId);

    /**
     * Receipt grouped by month for last 13 months
     *
     * @param userProfileId
     * @return
     */
    Iterator<ReceiptGrouped> getAllObjectsGroupedByMonth(String userProfileId);

    /**
     * Group receipts by location and sum up the total expense on that particular business location
     *
     * @param userProfileId
     * @return
     */
    Iterator<ReceiptGroupedByBizLocation> getAllReceiptGroupedByBizLocation(String userProfileId);

    @Deprecated
    List<String> findTitles(String title);

    ReceiptEntity findWithReceiptOCR(String receiptOCRId);

    void deleteSoft(ReceiptEntity object);

    /**
     * Count includes all active and inactive receipts
     *
     * @param bizStoreEntity
     * @return
     */
    long countAllReceiptForAStore(BizStoreEntity bizStoreEntity);

    /**
     * Count includes all active and inactive receipts
     *
     * @param bizNameEntity
     * @return
     */
    long countAllReceiptForABizName(BizNameEntity bizNameEntity);
}
