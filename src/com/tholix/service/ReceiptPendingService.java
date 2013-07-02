package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.web.form.PendingReceiptForm;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 4:37 AM
 */
@Service
public class ReceiptPendingService {

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
            GridFSDBFile gridFSDBFile = fileDBService.getFile(receiptEntityOCR.getReceiptBlobId());
            String originalFileName = (String) gridFSDBFile.getMetaData().get("original_fileName");
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
            GridFSDBFile gridFSDBFile = fileDBService.getFile(receiptEntityOCR.getReceiptBlobId());
            String originalFileName = (String) gridFSDBFile.getMetaData().get("original_fileName");
            pendingReceiptForm.addRejected(originalFileName, gridFSDBFile.getLength(), receiptEntityOCR);
        }
    }
}
