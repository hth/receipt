package com.tholix.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ItemOCRManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.repository.StorageManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.service.routes.ReceiptSenderJMS;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 1:09 PM
 */
@Service
public class ReceiptService {
    private static Logger log = Logger.getLogger(ReceiptService.class);

    @Autowired private ReceiptManager receiptManager;
    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private StorageManager storageManager;
    @Autowired private ItemManager itemManager;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private ReceiptSenderJMS senderJMS;

    /**
     * Find receipt for the id
     *
     * @param receiptId
     * @return
     */
    public ReceiptEntity findReceipt(String receiptId) {
        return receiptManager.findOne(receiptId);
    }

    /**
     *
     * @param dateTime
     * @param userProfileId
     * @return
     */
    public List<ReceiptEntity> findReceipt(DateTime dateTime, String userProfileId) {
        int year    = dateTime.getYear();
        int month   = dateTime.getMonthOfYear();
        int day     = dateTime.getDayOfMonth();

        return receiptManager.findThisDayReceipts(year, month, day, userProfileId);
    }

    /**
     * Find items for a receipt
     *
     * @param receiptEntity
     * @return
     */
    public List<ItemEntity> findItems(ReceiptEntity receiptEntity) {
        return itemManager.getWhereReceipt(receiptEntity);
    }

    /**
     * Delete a Receipt and its associated data
     * @param receiptId - Receipt id to delete
     */
    public boolean deleteReceipt(String receiptId) throws Exception {
        ReceiptEntity receipt = receiptManager.findOne(receiptId);
        if(receipt != null) {
            if(receipt.isActive()) {
                itemManager.deleteWhereReceipt(receipt);
                receiptManager.delete(receipt);
                storageManager.deleteObject(receipt.getReceiptBlobId());
                return true;
            } else {
                log.error("Attempt to delete inactive Receipt: " + receipt.getId() + ", Browser Back Action performed");
                throw new Exception("Receipt no longer exists");
            }
        }
        return false;
    }

    /**
     * Inactive the receipt and active ReceiptOCR. Delete all the ItemOCR and recreate from Items. Then delete all the items.
     * @param receiptId
     */
    public void reopen(String receiptId) throws Exception {
        try {
            ReceiptEntity receipt = receiptManager.findOne(receiptId);
            if(receipt.isActive()) {
                receipt.inActive();
                receiptManager.save(receipt);
                List<ItemEntity> items = itemManager.getWhereReceipt(receipt);

                ReceiptEntityOCR receiptOCR = receiptOCRManager.findOne(receipt.getReceiptOCRId());
                receiptOCR.active();
                receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_REQUEST);
                receiptOCR.setReceiptId(receipt.getId());
                receiptOCRManager.save(receiptOCR);
                itemOCRManager.deleteWhereReceipt(receiptOCR);

                List<ItemEntityOCR> ocrItems = getItemEntityFromItemEntityOCR(items, receiptOCR);
                itemOCRManager.saveObjects(ocrItems);
                itemManager.deleteWhereReceipt(receipt);

                log.info("ReceiptEntityOCR @Id after save: " + receiptOCR.getId());
                UserProfileEntity userProfile = userProfileManager.findOne(receiptOCR.getUserProfileId());
                senderJMS.send(receiptOCR, userProfile);
            } else {
                log.error("Attempt to invoke re-check on Receipt: " + receipt.getId() + ", Browser Back Action performed");
                throw new Exception("Receipt no longer exists");
            }
        } catch (Exception e) {
            log.error("Exception during customer requesting receipt recheck operation: " + e.getLocalizedMessage());
            throw e;
        }
    }



    /**
     * Used when data is read from Receipt and Item Entity during re-check process
     *
     * @param items
     * @param receiptOCR
     * @return
     */
    public List<ItemEntityOCR> getItemEntityFromItemEntityOCR(List<ItemEntity> items, ReceiptEntityOCR receiptOCR) {
        List<ItemEntityOCR> listOfItems = new ArrayList<>();

        for(ItemEntity item : items) {
            if(StringUtils.isNotEmpty(item.getName())) {
                ItemEntityOCR itemOCR = ItemEntityOCR.newInstance(item.getName(), item.getPrice().toString(), item.getTaxed(), item.getSequence(), receiptOCR, receiptOCR.getUserProfileId());
                itemOCR.setExpenseType(item.getExpenseType());
                itemOCR.setCreated(item.getCreated());
                itemOCR.setUpdated();

                itemOCR.setBizName(receiptOCR.getBizName());
                listOfItems.add(itemOCR);
            }
        }

        return listOfItems;
    }

    public void updateItemExpenseType(ItemEntity item) {
        itemManager.updateItemExpenseType(item);
    }
}
