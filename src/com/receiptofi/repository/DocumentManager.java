/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentEntity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender
 * @since Jan 6, 2013 1:29:22 PM
 *
 */
public interface DocumentManager extends RepositoryManager<DocumentEntity> {
	String TABLE = BaseEntity.getClassAnnotationValue(DocumentEntity.class, Document.class, "collection");

	long numberOfPendingReceipts(String userProfileId);

    DocumentEntity findOne(String documentId, String userProfileId);

    /**
     * Mostly used by technician and above level
     *
     * @param documentId
     * @return
     */
    DocumentEntity findActiveOne(String documentId);

    /**
     * Get all the pending receipts
     *
     * @param userProfileId
     * @return
     */
	List<DocumentEntity> getAllPending(String userProfileId);

    /**
     * Get all the rejected receipts
     *
     * @param userProfileId
     * @return
     */
    List<DocumentEntity> getAllRejected(String userProfileId);
}
