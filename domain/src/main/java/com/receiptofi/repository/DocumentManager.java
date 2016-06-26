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

    long numberOfPendingReceipts(String rid);

    long numberOfRejectedReceipts(String rid);

    DocumentEntity findDocumentByRid(String documentId, String rid);

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
     * @param rid
     * @return
     */
    List<DocumentEntity> getAllPending(String rid);

    /**
     * Total pending includes new and re-process document count.
     *
     * @return
     */
    long getTotalPending();

    /**
     * Documents processed today.
     *
     * @return
     */
    long getTotalProcessedToday();

    /**
     * Historical data on document processed from the days submitted.
     *
     * @param since
     * @return
     */
    Iterator<DocumentGrouped> getHistoricalStat(Date since);

    /**
     * Get all the rejected receipts.
     *
     * @param rid
     * @return
     */
    List<DocumentEntity> getAllRejected(String rid);

    List<DocumentEntity> getAllRejected(int purgeRejectedDocumentAfterDay);

    List<DocumentEntity> getAllProcessedDocuments();

    List<DocumentEntity> getDocumentsForNotification(int delay);

    void cloudUploadSuccessful(String documentId);

    void markNotified(String documentId);

    /**
     * Collection size.
     */
    long collectionSize();
}
