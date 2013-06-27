package com.tholix.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.CommentEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserProfileEntity;
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
public class ReceiptService {
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
                storageManager.deleteObject(receipt.getReceiptBlobId());

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
                    if(!StringUtils.isEmpty(receiptForm.getReceipt().getRecheckComment().getText())) {
                        CommentEntity comment = receiptForm.getReceipt().getRecheckComment();
                        if(StringUtils.isEmpty(comment.getId())) {
                            comment.setId(null);
                        }

                        commentManager.save(comment);
                        receipt.setRecheckComment(comment);
                    } else {
                        CommentEntity comment = receiptForm.getReceipt().getRecheckComment();
                        commentManager.deleteHard(comment);
                        receipt.setRecheckComment(null);
                    }

                    if(!StringUtils.isEmpty(receiptForm.getReceipt().getNotes().getText())) {
                        CommentEntity comment = receiptForm.getReceipt().getNotes();
                        if(StringUtils.isEmpty(comment.getId())) {
                            comment.setId(null);
                        }

                        commentManager.save(comment);
                        receipt.setNotes(comment);
                    } else {
                        CommentEntity comment = receiptForm.getReceipt().getNotes();
                        commentManager.deleteHard(comment);
                        receipt.setNotes(null);
                    }

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

    /**
     * Updates the ItemEntity with changed ExpenseType
     *
     * @param item
     */
    public void updateItemWithExpenseType(ItemEntity item) throws Exception {
        itemManager.updateItemWithExpenseType(item);
    }
}
