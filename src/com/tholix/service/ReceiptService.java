package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.StorageManager;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 1:09 PM
 */
@Service
public class ReceiptService {

    @Autowired private ReceiptManager receiptManager;
    @Autowired private StorageManager storageManager;
    @Autowired private ItemManager itemManager;

    /**
     * Show receipt and associated items
     *
     * @param receiptId
     * @param receiptEntity
     * @param items
     */
    public void showReceipt(String receiptId, ReceiptEntity receiptEntity, List<ItemEntity> items) {
        receiptEntity = receiptManager.findOne(receiptId);
        items = itemManager.getWhereReceipt(receiptEntity);
    }


    /**
     * Delete a Receipt and its associated data
     * @param receiptId - Receipt id to delete
     */
    public boolean deleteReceipt(String receiptId) {
        ReceiptEntity receiptForm;
        receiptForm = receiptManager.findOne(receiptId);
        if(receiptForm != null) {
            itemManager.deleteWhereReceipt(receiptForm);
            receiptManager.delete(receiptForm);
            storageManager.deleteObject(receiptForm.getReceiptBlobId());
            return true;
        }
        return false;
    }
}
