package com.receiptofi.service;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ReceiptEntityOCR;
import com.receiptofi.repository.ReceiptOCRManager;
import com.receiptofi.web.form.PendingReceiptForm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

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
            for(FileSystemEntity scaledId : receiptEntityOCR.getReceiptScaledBlobId()) {
                GridFSDBFile gridFSDBFile = fileDBService.getFile(scaledId.getBlobId());
                String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
                pendingReceiptForm.addPending(originalFileName, gridFSDBFile.getLength(), receiptEntityOCR);
            }
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
            for(FileSystemEntity scaledId : receiptEntityOCR.getReceiptScaledBlobId()) {
                GridFSDBFile gridFSDBFile = fileDBService.getFile(scaledId.getBlobId());
                String originalFileName = (String) gridFSDBFile.getMetaData().get("ORIGINAL_FILENAME");
                pendingReceiptForm.addRejected(originalFileName, gridFSDBFile.getLength(), receiptEntityOCR);
            }
        }
    }
}
