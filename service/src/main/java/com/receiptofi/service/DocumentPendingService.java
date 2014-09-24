package com.receiptofi.service;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.repository.DocumentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 4:37 AM
 */
@Service
public final class DocumentPendingService {

    @Autowired private DocumentManager documentManager;

    /**
     * All pending receipt for a user
     *
     * @param userProfileId
     * @return
     */
    public List<DocumentEntity> getAllPending(String userProfileId) {
        return documentManager.getAllPending(userProfileId);
    }

    /**
     * All pending receipt for a user
     *
     * @param userProfileId
     * @return
     */
    public List<DocumentEntity> getAllRejected(String userProfileId) {
        return documentManager.getAllRejected(userProfileId);
    }
}
