package com.receiptofi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.ReceiptEntityOCR;
import com.receiptofi.repository.ReceiptOCRManager;
import com.receiptofi.web.form.PendingReceiptForm;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 4:37 AM
 */
@Service
public final class ReceiptPendingService {

    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private FileDBService fileDBService;

    /**
     * All pending receipt for a user
     *
     * @param userProfileId
     * @return
     */
    public void getAllPending(String userProfileId, PendingReceiptForm pendingReceiptForm) {
        List<ReceiptEntityOCR> receiptEntityOCRList = receiptOCRManager.getAllPending(userProfileId);
        for(ReceiptEntityOCR receiptEntityOCR : receiptEntityOCRList) {
            GridFSDBFile gridFSDBFile = fileDBService.getFile(receiptEntityOCR.getReceiptScaledBlobId());
            String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
            pendingReceiptForm.addPending(originalFileName, gridFSDBFile.getLength(), receiptEntityOCR);
        }
    }

    /**
     * All pending receipt for a user
     *
     * @param userProfileId
     * @return
     */
    public void getAllRejected(String userProfileId, PendingReceiptForm pendingReceiptForm) {
        List<ReceiptEntityOCR> receiptEntityOCRList = receiptOCRManager.getAllRejected(userProfileId);
        for(ReceiptEntityOCR receiptEntityOCR : receiptEntityOCRList) {
            GridFSDBFile gridFSDBFile = fileDBService.getFile(receiptEntityOCR.getReceiptScaledBlobId());
            String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
            pendingReceiptForm.addRejected(originalFileName, gridFSDBFile.getLength(), receiptEntityOCR);
        }
    }
}
