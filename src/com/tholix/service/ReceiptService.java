package com.tholix.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.CommentEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.CommentTypeEnum;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.repository.CommentManager;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ItemOCRManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.repository.StorageManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.service.routes.ReceiptSenderJMS;
import com.tholix.web.form.ReceiptForm;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 1:09 PM
 */
@Service
public final class ReceiptService {
    private static Logger log = Logger.getLogger(ReceiptService.class);

    @Autowired private ReceiptManager receiptManager;
    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private StorageManager storageManager;
    @Autowired private ItemManager itemManager;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private ReceiptSenderJMS senderJMS;
    @Autowired private CommentManager commentManager;

    /**
     * Find receipt for a receipt id for a specific user profile id
     *
     * @param receiptId
     * @return
     */
    public ReceiptEntity findReceipt(String receiptId, String userProfileId) {
        return receiptManager.findReceipt(receiptId, userProfileId);
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
     * //TODO mark the items as deleted but do not delete Item and Receipt
     * Delete a Receipt and its associated data
     * @param receiptId - Receipt id to delete
     *
     * TODO make sure delete request comes with a user profile id. Check if the user is deleting its own receipt.
     */
    public boolean deleteReceipt(String receiptId) throws Exception {
        ReceiptEntity receipt = receiptManager.findOne(receiptId);
        if(receipt != null) {
            if(receipt.isActive()) {
                itemManager.deleteSoft(receipt);
                storageManager.deleteSoft(receipt.getReceiptBlobId());

                if(receipt.getRecheckComment() != null && !StringUtils.isEmpty(receipt.getRecheckComment().getId())) {
                    commentManager.deleteHard(receipt.getRecheckComment());
                }
                if(receipt.getNotes() != null && !StringUtils.isEmpty(receipt.getNotes().getId())) {
                    commentManager.deleteHard(receipt.getNotes());
                }

                if(!StringUtils.isEmpty(receipt.getReceiptOCRId())) {
                    ReceiptEntityOCR receiptEntityOCR = receiptOCRManager.findOne(receipt.getReceiptOCRId());
                    if(receiptEntityOCR != null) {
                        itemOCRManager.deleteWhereReceipt(receiptEntityOCR);
                        receiptOCRManager.deleteHard(receiptEntityOCR);
                        receipt.setReceiptOCRId(null);
                    }
                }

                receiptManager.deleteSoft(receipt);
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
     * @param receiptForm
     */
    public void reopen(ReceiptForm receiptForm) throws Exception {
        try {
            ReceiptEntity receipt = receiptManager.findOne(receiptForm.getReceipt().getId());
            if(receipt.getReceiptOCRId() != null) {
                if(receipt.isActive()) {
                    receipt.inActive();
                    List<ItemEntity> items = itemManager.getWhereReceipt(receipt);

                    ReceiptEntityOCR receiptOCR = receiptOCRManager.findOne(receipt.getReceiptOCRId());
                    receiptOCR.active();
                    receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_REQUEST);
                    receiptOCR.setRecheckComment(receipt.getRecheckComment());
                    receiptOCR.setNotes(receipt.getNotes());

                    /** All activity at the end is better because you never know what could go wrong during populating other data */
                    receiptManager.save(receipt);
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
            } else {
                log.error("No receiptOCR id found in Receipt: " + receipt.getId() + ", aborting the reopen process");
                throw new Exception("Receipt could not be requested for Re-Check. Contact administrator with Receipt # " + receipt.getId() + ", contact Administrator with the Id");
            }
        } catch (Exception e) {
            log.error("Exception during customer requesting receipt recheck operation: " + e.getLocalizedMessage());

            //Need to send a well formatted error message to customer instead of jumbled mumbled exception stacktrace
            throw new Exception("Exception occurred during requesting receipt recheck operation for Receipt # " + receiptForm.getReceipt().getId() + ", contact Administrator with the Id");
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
                ItemEntityOCR itemOCR = ItemEntityOCR.newInstance();
                itemOCR.setName(item.getName());
                itemOCR.setPrice(item.getPrice().toString());
                itemOCR.setTaxed(item.getTaxed());
                itemOCR.setSequence(item.getSequence());
                itemOCR.setReceipt(receiptOCR);
                itemOCR.setUserProfileId(receiptOCR.getUserProfileId());
                itemOCR.setExpenseType(item.getExpenseType());
                itemOCR.setCreated(item.getCreated());
                itemOCR.setQuantity(item.getQuantity());
                itemOCR.setUpdated();

                itemOCR.setBizName(receiptOCR.getBizName());
                listOfItems.add(itemOCR);
            }
        }

        return listOfItems;
    }

    /**
     * Updates the ItemEntity with changed ExpenseType
     *
     * @param item
     */
    public void updateItemWithExpenseType(ItemEntity item) throws Exception {
        itemManager.updateItemWithExpenseType(item);
    }

    /**
     * Saves notes to receipt
     *
     * @param notes
     * @param receiptId
     * @param userProfileId
     * @return
     */
    public boolean updateNotes(String notes, String receiptId, String userProfileId) {
        ReceiptEntity receiptEntity = receiptManager.findReceipt(receiptId, userProfileId);
        CommentEntity commentEntity = receiptEntity.getNotes();
        boolean commentEntityBoolean = false;
        if(commentEntity == null) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(CommentTypeEnum.NOTES);
            commentEntity.setText(notes);
        } else {
            commentEntity.setText(notes);
        }
        try {
            commentEntity.setUpdated();
            commentManager.save(commentEntity);
            if(commentEntityBoolean) {
                receiptEntity.setNotes(commentEntity);
                receiptManager.save(receiptEntity);
            }
            return true;
        } catch (Exception exce) {
            log.error("Failed updating notes for receipt: " + receiptId);
            return false;
        }
    }

    /**
     * Saves recheck comment to receipt
     *
     * @param comment
     * @param receiptId
     * @param userProfileId
     * @return
     */
    public boolean updateComment(String comment, String receiptId, String userProfileId) {
        ReceiptEntity receiptEntity = receiptManager.findReceipt(receiptId, userProfileId);
        CommentEntity commentEntity = receiptEntity.getRecheckComment();
        boolean commentEntityBoolean = false;
        if(commentEntity == null) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(CommentTypeEnum.RECHECK);
            commentEntity.setText(comment);
        } else {
            commentEntity.setText(comment);
        }
        try {
            commentEntity.setUpdated();
            commentManager.save(commentEntity);
            if(commentEntityBoolean) {
                receiptEntity.setRecheckComment(commentEntity);
                receiptManager.save(receiptEntity);
            }
            return true;
        } catch (Exception exce) {
            log.error("Failed updating comment for receipt: " + receiptId);
            return false;
        }
    }

    /**
     * Saves recheck comment to receipt OCR
     *
     * @param comment
     * @param receiptOCRId
     * @return
     */
    public boolean updateOCRComment(String comment, String receiptOCRId) {
        ReceiptEntityOCR receiptEntityOCR = receiptOCRManager.findOne(receiptOCRId);
        CommentEntity commentEntity = receiptEntityOCR.getRecheckComment();
        boolean commentEntityBoolean = false;
        if(commentEntity == null) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(CommentTypeEnum.RECHECK);
            commentEntity.setText(comment);
        } else {
            commentEntity.setText(comment);
        }
        try {
            commentEntity.setUpdated();
            commentManager.save(commentEntity);
            if(commentEntityBoolean) {
                receiptEntityOCR.setRecheckComment(commentEntity);
                receiptOCRManager.save(receiptEntityOCR);
            }
            return true;
        } catch (Exception exce) {
            log.error("Failed updating comment for receiptOCR: " + receiptOCRId);
            return false;
        }
    }

    /**
     *
     * @param bizNameEntity
     * @param userProfileId
     * @return
     */
    public List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String userProfileId) {
        return receiptManager.findReceipt(bizNameEntity, userProfileId);
    }

    /**
     * Counts all the valid and invalid receipt that has referred the store
     *
     * @param bizStoreEntity
     * @return
     */
    public long countAllReceiptForAStore(BizStoreEntity bizStoreEntity) {
        return receiptManager.countAllReceiptForAStore(bizStoreEntity);
    }

    /**
     * Counts all the valid and invalid receipt that has referred the biz name
     *
     * @param bizNameEntity
     * @return
     */
    public long countAllReceiptForABizName(BizNameEntity bizNameEntity) {
        return receiptManager.countAllReceiptForABizName(bizNameEntity);
    }
}
