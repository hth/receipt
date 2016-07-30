/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.domain.value.ReceiptListViewGrouped;

import org.bson.types.ObjectId;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author hitender
 * @since Dec 26, 2012 3:09:48 PM
 */
public interface ReceiptManager extends RepositoryManager<ReceiptEntity> {

    /**
     * Used mostly for loading receipt by Id. Should be refrained from using as this query is not secure.
     *
     * @param receiptId
     * @return
     */
    ReceiptEntity findReceipt(String receiptId);

    /**
     * @param receiptId
     * @param rid
     * @return
     */
    ReceiptEntity findReceipt(String receiptId, String rid);

    /**
     * Use this method when doing recheck.
     *
     * @param receiptId
     * @param rid
     * @return
     */
    ReceiptEntity findReceiptWhileRecheck(String receiptId, String rid);

    /**
     * Get receipt for specific user ignoring other status of this receipt.
     *
     * @param receiptId
     * @param rid
     * @return
     */
    @Mobile
    ReceiptEntity findReceiptForMobile(String receiptId, String rid);

    /**
     * Get receipt for specific user.
     *
     * @param receiptId
     * @param rid
     * @return
     */
    ReceiptEntity getReceipt(String receiptId, String rid);

    /**
     * Find all receipts with BizName for the user
     *
     * @param bizNameEntity
     * @param receiptUserId
     * @return
     */
    List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String receiptUserId, DateTime receiptForMonth);

    /**
     * Gets all the user receipts. Refrain from using open ended query.
     *
     * @param rid
     * @return
     */
    List<ReceiptEntity> getAllReceipts(String rid);

    /**
     * Get receipts only the selected year.
     *
     * @param rid
     * @param startOfTheYear
     * @return
     */
    List<ReceiptEntity> getAllReceiptsForTheYear(String rid, DateTime startOfTheYear);

    /**
     * Gets user receipts for current month.
     *
     * @param rid
     * @return
     */
    List<ReceiptEntity> getAllReceiptsForThisMonth(String rid, DateTime monthYear);

    /**
     * Get receipts associated with year, month, day.
     *
     * @param year
     * @param month
     * @param day
     * @param receiptUserId
     * @return
     */
    List<ReceiptEntity> findThisDayReceipts(int year, int month, int day, String receiptUserId);

    /**
     * Receipt grouped by day.
     *
     * @param rid
     * @return
     */
    Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String rid);

    /**
     * Receipt grouped by month for last 13 months.
     *
     * @param rid
     * @return
     */
    List<ReceiptGrouped> getReceiptGroupedByMonth(String rid);

    /**
     * Gets all receipts for month.
     *
     * @param rid
     * @param month
     * @param year
     * @return
     */
    List<ReceiptListViewGrouped> getReceiptForGroupedByMonth(String rid, int month, int year);

    /**
     * Group receipts by location and sum up the total expense on that particular business location.
     *
     * @param rid
     * @return
     */
    Iterator<ReceiptGroupedByBizLocation> getAllReceiptGroupedByBizLocation(String rid);

    ReceiptEntity findWithReceiptOCR(String documentId);

    void deleteSoft(ReceiptEntity object);

    /**
     * Count includes all active and inactive receipts.
     *
     * @param bizStoreEntity
     * @return
     */
    long countAllReceiptForAStore(BizStoreEntity bizStoreEntity);

    /**
     * Count includes all active and inactive receipts.
     *
     * @param bizNameEntity
     * @return
     */
    long countAllReceiptForABizName(BizNameEntity bizNameEntity);

    /**
     * Check if not deleted receipt exists.
     *
     * @param checksum
     * @return
     */
    boolean notDeletedChecksumDuplicate(String checksum, String id);

    /**
     * Find if receipt with similar checksum exists.
     *
     * @param checksum
     * @return
     */
    boolean hasRecordWithSimilarChecksum(String checksum);

    /**
     * Find existing receipt with similar details.
     *
     * @param rid
     * @param receiptDate
     * @param total
     * @return
     */
    ReceiptEntity findOne(String rid, Date receiptDate, Double total);

    /**
     * Removes reference to expensofi file name in file system. This is expense excel file generated by user.
     *
     * @param filename
     */
    void removeExpensofiFilenameReference(String filename);

    List<ReceiptEntity> findAllReceipts(String rid);

    long countReceiptsUsingExpenseType(String expenseTypeId, String rid);

    /**
     * Remove reference to expense tag.
     *
     * @param rid
     * @param expenseTagId
     * @return
     */
    boolean removeExpenseTagReferences(String rid, String expenseTagId);

    /**
     * Updates receipts created while splitting expenses. Updates only those receipts that is not deleted and is active.
     *
     * @param referToReceiptId
     * @param splitCount
     * @param splitTotal
     * @param splitTax
     * @return
     */
    boolean updateFriendReceipt(String referToReceiptId, int splitCount, Double splitTotal, Double splitTax);

    /**
     * Delete shared receipts with friend.
     *
     * @param receiptId
     * @param rid
     * @return
     */
    boolean softDeleteFriendReceipt(String receiptId, String rid);

    /**
     * Increase split count for matching receiptId.
     *
     * @param receiptId
     * @param splitTotal
     * @param splitTax
     * @return
     */
    boolean increaseSplitCount(String receiptId, double splitTotal, double splitTax);

    /**
     * Decrease split count for matching receiptId.
     *
     * @param receiptId
     * @param splitTotal
     * @param splitTax
     * @return
     */
    boolean decreaseSplitCount(String receiptId, double splitTotal, double splitTax);

    /**
     * Find all receipt that are referred with matching receipt id.
     *
     * @param receiptId
     * @return
     */
    List<ReceiptEntity> findAllReceiptWithMatchingReferReceiptId(String receiptId);

    List<ReceiptEntity> getReceiptsWithoutQC();

    /**
     * Collection size.
     */
    long collectionSize();

    /**
     *
     * @param rid
     * @param expenseTags
     * @param delayDuration delay by number of days
     * @return
     */
    List<ReceiptEntity> getReceiptsWithExpenseTags(String rid, List<ObjectId> expenseTags, int delayDuration);
}
