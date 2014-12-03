/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.value.DocumentGrouped;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author hitender
 * @since Jan 6, 2013 1:29:22 PM
 */
public interface DocumentManager extends RepositoryManager<DocumentEntity> {

    long numberOfPendingReceipts(String receiptUserId);

    long numberOfRejectedReceipts(String receiptUserId);

    DocumentEntity findOne(String documentId, String receiptUserId);

    /**
     * Mostly used by technician and above level.
     *
     * @param documentId
     * @return
     */
    DocumentEntity findActiveOne(String documentId);

    /**
     * Mostly used by user to delete rejected documents.
     *
     * @param documentId
     * @return
     */
    DocumentEntity findRejectedOne(String documentId);

    /**
     * Get all the pending receipts.
     *
     * @param receiptUserId
     * @return
     */
    List<DocumentEntity> getAllPending(String receiptUserId);

    /**
     * Document all pending.
     * @return
     */
    long getTotalPending();

    /**
     * Documents processed today.
     * @return
     */
    long getTotalProcessedToday();

    /**
     * Historical data on document processed from the days submitted.
     * @param since
     * @return
     */
    Iterator<DocumentGrouped> getHistoricalStat(Date since);

    /**
     * Get all the rejected receipts.
     *
     * @param receiptUserId
     * @return
     */
    List<DocumentEntity> getAllRejected(String receiptUserId);

    List<DocumentEntity> getAllRejected(int purgeRejectedDocumentAfterDay);

    List<DocumentEntity> getAllProcessedDocuments();

    void cloudUploadSuccessful(String documentId);

    /**
     * Collection size.
     */
    long collectionSize();
}
