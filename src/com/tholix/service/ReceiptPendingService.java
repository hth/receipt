package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.repository.ReceiptOCRManager;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 4:37 AM
 */
@Service
public class ReceiptPendingService {

    @Autowired private ReceiptOCRManager receiptOCRManager;

    /**
     * All pending receipt for a user
     *
     * @param userProfileId
     * @return
     */
    public List<ReceiptEntityOCR> getAllPending(String userProfileId) {
        return receiptOCRManager.getAllObjects(userProfileId);
    }
}
