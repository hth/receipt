package com.tholix.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import com.tholix.domain.CommentEntity;
import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.repository.CommentManager;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ItemOCRManager;
import com.tholix.repository.MessageManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;
import com.tholix.repository.StorageManager;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 2:09 PM
 */
@Service
public class ReceiptUpdateService {
    private static final Logger log = Logger.getLogger(ReceiptUpdateService.class);

    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private ItemOCRManager itemOCRManager;

    @Autowired private ReceiptManager receiptManager;
    @Autowired private ItemManager itemManager;
    @Autowired private MessageManager messageManager;
    @Autowired private AdminLandingService adminLandingService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private CommentManager commentManager;
    @Autowired private NotificationService notificationService;
    @Autowired private StorageManager storageManager;

    public ReceiptEntityOCR loadReceiptOCRById(String id) {
        return receiptOCRManager.findOne(id);
    }

    public List<ItemEntityOCR> loadItemsOfReceipt(ReceiptEntityOCR receiptEntity) {
        return itemOCRManager.getWhereReceipt(receiptEntity);
    }

    /**
     * This method is used when technician saves the processed receipt for the first time
     *
     * @param receipt
     * @param items
     * @param receiptOCR
     * @throws Exception
     */
    @Transactional(rollbackFor={Exception.class})
    public void turkReceipt(ReceiptEntity receipt, List<ItemEntity> items, ReceiptEntityOCR receiptOCR) throws Exception {
        try {
            adminLandingService.saveNewBusinessAndOrStore(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            adminLandingService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
            receiptOCR.setReceiptId(receipt.getId());
            receiptOCR.inActive();
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), ReceiptStatusEnum.OCR_PROCESSED, ReceiptStatusEnum.TURK_PROCESSED);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.OCR_PROCESSED);
                throw exce;
            }

            notificationService.addNotification("Receipt processed '" + receipt.getBizName().getName() + "'", receiptOCR.getUserProfileId());
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            log.warn("Revert all the transaction for Receipt: " + receipt.getId() + ", ReceiptOCR: " + receiptOCR.getId());

            //For rollback
            if(StringUtils.isNotEmpty(receipt.getId())) {
                long sizeReceiptInitial = receiptManager.collectionSize();
                long sizeItemInitial = itemManager.collectionSize();

                itemManager.deleteWhereReceipt(receipt);
                receiptManager.deleteHard(receipt);

                long sizeReceiptFinal = receiptManager.collectionSize();
                long sizeItemFinal = itemManager.collectionSize();
                if(sizeReceiptInitial != sizeReceiptFinal) {
                    log.warn("Initial receipt size: " + sizeReceiptInitial + ", Final receipt size: " + sizeReceiptFinal + ". Removed Receipt: " + receipt.getId());
                } else {
                    log.warn("Initial receipt size and Final receipt size are same: '" + sizeReceiptInitial + "' : '" + sizeReceiptFinal + "'");
                }

                if(sizeItemInitial != sizeItemFinal) {
                    log.warn("Initial item size: " + sizeItemInitial + ", Final item size: " + sizeItemFinal);
                } else {
                    log.warn("Initial item size and Final item size are same: '" + sizeItemInitial + "' : '" + sizeItemFinal + "'");
                }

                receiptOCR.setReceiptStatus(ReceiptStatusEnum.OCR_PROCESSED);
                receiptOCRManager.save(receiptOCR);
                //log.error("Failed to rollback Receipt OCR: " + receiptOCR.getId() + ", error message: " + e.getLocalizedMessage());

                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.OCR_PROCESSED);
                //End of roll back

                log.info("Complete with rollback: throwing exception");
            }
            throw new Exception(exce.getLocalizedMessage());
        }
    }

    /**
     * This method is executed when Technician is re-checking the receipt
     *
     * @param receipt
     * @param items
     * @param receiptOCR
     * @throws Exception
     */
    @Transactional(rollbackFor={Exception.class})
    public void turkReceiptReCheck(ReceiptEntity receipt, List<ItemEntity> items, ReceiptEntityOCR receiptOCR) throws Exception {
        try {
            adminLandingService.saveNewBusinessAndOrStore(receipt);
            if(StringUtils.isNotEmpty(receipt.getId())) {
                ReceiptEntity fetchedReceipt = receiptManager.findOne(receipt.getId());
                if(fetchedReceipt == null) {
                    // By creating new receipt with old id, we move the pending receipt from the list back to users account
                    log.warn("Something had gone wrong with original receipt id: " + receipt.getId() + ", so creating another with old receipt id");
                } else {
                    receipt.setVersion(fetchedReceipt.getVersion());
                    receipt.setCreated(fetchedReceipt.getCreated());
                }
            }

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            adminLandingService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
            receiptOCR.inActive();

            //On recheck comments are updated by technician. Receipt notes are never modified
            if(!StringUtils.isEmpty(receiptOCR.getRecheckComment().getText())) {
                CommentEntity comment = receiptOCR.getRecheckComment();
                if(StringUtils.isEmpty(comment.getId())) {
                    comment.setId(null);
                }

                commentManager.save(comment);
                receiptOCR.setRecheckComment(comment);
                receipt.setRecheckComment(comment);
            } else {
                CommentEntity comment = receiptOCR.getRecheckComment();
                commentManager.deleteHard(comment);
                receiptOCR.setRecheckComment(null);
                receipt.setRecheckComment(null);
            }

            //Since Technician cannot change notes at least we gotta make sure we are not adding one when the Id for notes are missing
            if(StringUtils.isEmpty(receiptOCR.getNotes().getId())) {
                receiptOCR.setNotes(null);
                receipt.setNotes(null);
            }

            receiptManager.save(receipt);
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), ReceiptStatusEnum.TURK_REQUEST, ReceiptStatusEnum.TURK_PROCESSED);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.TURK_REQUEST);
                throw exce;
            }

            notificationService.addNotification("Receipt re-checked '" + receipt.getBizName().getName() + "'", receiptOCR.getUserProfileId());
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            log.warn("Revert all the transaction for Receipt: " + receipt.getId() + ", ReceiptOCR: " + receiptOCR.getId());

            //For rollback
            if(StringUtils.isNotEmpty(receipt.getId())) {
                long sizeReceiptInitial = receiptManager.collectionSize();
                long sizeItemInitial = itemManager.collectionSize();

                itemManager.deleteWhereReceipt(receipt);
                receiptManager.deleteHard(receipt);

                long sizeReceiptFinal = receiptManager.collectionSize();
                long sizeItemFinal = itemManager.collectionSize();
                if(sizeReceiptInitial != sizeReceiptFinal) {
                    log.warn("Initial receipt size: " + sizeReceiptInitial + ", Final receipt size: " + sizeReceiptFinal + ". Removed Receipt: " + receipt.getId());
                } else {
                    log.warn("Initial receipt size and Final receipt size are same: '" + sizeReceiptInitial + "' : '" + sizeReceiptFinal + "'");
                }

                if(sizeItemInitial != sizeItemFinal) {
                    log.warn("Initial item size: " + sizeItemInitial + ", Final item size: " + sizeItemFinal);
                } else {
                    log.warn("Initial item size and Final item size are same: '" + sizeItemInitial + "' : '" + sizeItemFinal + "'");
                }

                receiptOCR.setReceiptStatus(ReceiptStatusEnum.OCR_PROCESSED);
                receiptOCRManager.save(receiptOCR);
                //log.error("Failed to rollback Receipt OCR: " + receiptOCR.getId() + ", error message: " + e.getLocalizedMessage());

                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.TURK_REQUEST);
                //End of roll back

                log.info("Complete with rollback: throwing exception");
            }
            throw new Exception(exce.getLocalizedMessage());
        }
    }

    /**
     * Reject receipt when invalid or un-readable
     *
     * @param receiptOCR
     * @throws Exception
     */
    @Transactional(rollbackFor={Exception.class})
    public void turkReject(ReceiptEntityOCR receiptOCR) throws Exception {
        try {
            receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_RECEIPT_REJECT);
            receiptOCR.setBizName(null);
            receiptOCR.setBizStore(null);
            receiptOCR.inActive();
            receiptOCR.markAsdeleted();
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), ReceiptStatusEnum.OCR_PROCESSED, ReceiptStatusEnum.TURK_RECEIPT_REJECT);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_RECEIPT_REJECT, ReceiptStatusEnum.OCR_PROCESSED);
                throw exce;
            }
            itemOCRManager.deleteWhereReceipt(receiptOCR);

            storageManager.deleteSoft(receiptOCR.getReceiptBlobId());
            GridFSDBFile gridFSDBFile = storageManager.get(receiptOCR.getReceiptBlobId());
            DBObject dbObject =  gridFSDBFile.getMetaData();
            notificationService.addNotification("Could not process receipt '" + dbObject.get("original_fileName") + "'", receiptOCR.getUserProfileId());
        } catch(Exception exce) {
            log.error("Rejection of a receipt failed: " + exce.getLocalizedMessage());
            log.warn("Revert all the transaction for ReceiptOCR: " + receiptOCR.getId());

            receiptOCR.setReceiptStatus(ReceiptStatusEnum.OCR_PROCESSED);
            receiptOCR.active();
            receiptOCRManager.save(receiptOCR);
            //log.error("Failed to rollback Receipt OCR: " + receiptOCR.getId() + ", error message: " + e.getLocalizedMessage());

            messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_RECEIPT_REJECT, ReceiptStatusEnum.OCR_PROCESSED);
            //End of roll back

            log.info("Complete with rollback: throwing exception");
        }
    }

    /**
     * Delete all the associated data with Receipt OCR like Item OCR, and
     * Message Receipt Entity OCR including deletion of with Receipt OCR
     * But cannot delete ReceiptOCR when the receipt has been processed once and now it pending for re-check
     *
     *
     * @param receiptOCR
     */
    public void deletePendingReceiptOCR(ReceiptEntityOCR receiptOCR) {
        ReceiptEntityOCR receiptEntityOCR = receiptOCRManager.findOne(receiptOCR.getId());
        if(StringUtils.isEmpty(receiptEntityOCR.getReceiptId())) {
            receiptOCRManager.deleteHard(receiptEntityOCR);
            itemOCRManager.deleteWhereReceipt(receiptEntityOCR);
            messageManager.deleteAllForReceiptOCR(receiptEntityOCR.getId());
            storageManager.deleteHard(receiptEntityOCR.getReceiptBlobId());
        } else {
            log.warn("User trying to delete processed Receipt OCR #: " + receiptEntityOCR.getId() + ", Receipt Id #:" + receiptEntityOCR.getReceiptId());
        }
    }

    /**
     * Populates items with BizNameEntity
     *
     * @param items
     * @param receiptEntity
     */
    private void populateItemsWithBizName(List<ItemEntity> items, ReceiptEntity receiptEntity) {
        for(ItemEntity item : items) {
            item.setBizName(receiptEntity.getBizName());
            populateWithExpenseType(item);
        }
    }

    /**
     * when Items are populated with just an Id of the expenseType. This normally happens during Re-Check condition.
     * The following code makes sures objects are populated with just not id but with complete object instead
     * //TODO in future keep an eye on this object as during save of an ItemEntity the @DBRef expenseType is saved as Id instead of an object. As of now it is saved and updated
     *
     * @param item
     */
    private void populateWithExpenseType(ItemEntity item) {
        if(item.getExpenseType() != null && item.getExpenseType().getId() != null) {
            ExpenseTypeEntity expenseType = userProfilePreferenceService.getExpenseType(item.getExpenseType().getId());
            item.setExpenseType(expenseType);
        }
    }
}
