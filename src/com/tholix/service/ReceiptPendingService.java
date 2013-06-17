package com.tholix.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.repository.StorageManager;
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
    public List<PendingReceiptForm> getAllPending(String userProfileId) {
        List<ReceiptEntityOCR> receiptEntityOCRList = receiptOCRManager.getAllObjects(userProfileId);
        List<PendingReceiptForm> pendingReceiptFormList = new ArrayList<>();
        for(ReceiptEntityOCR receiptEntityOCR : receiptEntityOCRList) {
            GridFSDBFile gridFSDBFile = fileDBService.getFile(receiptEntityOCR.getReceiptBlobId());
            String originalFileName = (String) gridFSDBFile.getMetaData().get("original_fileName");
            PendingReceiptForm pendingReceiptForm = PendingReceiptForm.newInstance(originalFileName, gridFSDBFile.getLength(), receiptEntityOCR);
            pendingReceiptFormList.add(pendingReceiptForm);
        }
        return  pendingReceiptFormList;
    }
}
