package com.receiptofi.service;

import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.ReceiptEntityOCR;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.CommentManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.MessageManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.ReceiptOCRManager;
import com.receiptofi.repository.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 2:09 PM
 */
@Service
public final class ReceiptUpdateService {
    private static final Logger log = LoggerFactory.getLogger(ReceiptUpdateService.class);

    @Autowired private ReceiptOCRManager receiptOCRManager;
    @Autowired private ItemOCRManager itemOCRManager;

    @Autowired private ReceiptManager receiptManager;
    @Autowired private ItemManager itemManager;
    @Autowired private MessageManager messageManager;
    @Autowired private BizService bizService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private CommentManager commentManager;
    @Autowired private NotificationService notificationService;
    @Autowired private StorageManager storageManager;
    @Autowired private FileSystemService fileSystemService;

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
            ReceiptEntityOCR receiptEntityOCR = receiptOCRManager.findOne(receiptOCR.getId());

            receipt.setImageOrientation(receiptEntityOCR.getImageOrientation());
            receipt.setReceiptBlobId(receiptEntityOCR.getReceiptBlobId());
            receipt.setReceiptScaledBlobId(receiptEntityOCR.getReceiptScaledBlobId());

            receiptOCR.setImageOrientation(receiptEntityOCR.getImageOrientation());
            receiptOCR.setReceiptBlobId(receiptEntityOCR.getReceiptBlobId());
            receiptOCR.setReceiptScaledBlobId(receiptEntityOCR.getReceiptScaledBlobId());

            //update the version number as the value could have changed by rotating receipt image through ajax
            receiptOCR.setVersion(receiptEntityOCR.getVersion());

            bizService.saveNewBusinessAndOrStore(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setDocumentStatus(DocumentStatusEnum.TURK_PROCESSED);
            receiptOCR.setReceiptId(receipt.getId());
            receiptOCR.inActive();
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), DocumentStatusEnum.OCR_PROCESSED, DocumentStatusEnum.TURK_PROCESSED);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, DocumentStatusEnum.TURK_PROCESSED, DocumentStatusEnum.OCR_PROCESSED);
                throw exce;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(receipt.getTotalString());
            sb.append(" '").append(receipt.getBizName().getName()).append("' ");
            sb.append("receipt processed");
            notificationService.addNotification(sb.toString(), NotificationTypeEnum.RECEIPT, receipt);

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

                receiptOCR.setDocumentStatus(DocumentStatusEnum.OCR_PROCESSED);
                receiptOCRManager.save(receiptOCR);
                //log.error("Failed to rollback Receipt OCR: " + receiptOCR.getId() + ", error message: " + e.getLocalizedMessage());

                messageManager.undoUpdateObject(receiptOCR.getId(), false, DocumentStatusEnum.TURK_PROCESSED, DocumentStatusEnum.OCR_PROCESSED);
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
        ReceiptEntity fetchedReceipt = null;
        try {
            ReceiptEntityOCR receiptEntityOCR = receiptOCRManager.findOne(receiptOCR.getId());

            receipt.setImageOrientation(receiptEntityOCR.getImageOrientation());
            receipt.setReceiptBlobId(receiptEntityOCR.getReceiptBlobId());
            receipt.setReceiptScaledBlobId(receiptEntityOCR.getReceiptScaledBlobId());

            receiptOCR.setImageOrientation(receiptEntityOCR.getImageOrientation());
            receiptOCR.setReceiptBlobId(receiptEntityOCR.getReceiptBlobId());
            receiptOCR.setReceiptScaledBlobId(receiptEntityOCR.getReceiptScaledBlobId());

            //update the version number as the value could have changed by rotating receipt image through ajax
            receiptOCR.setVersion(receiptEntityOCR.getVersion());

            bizService.saveNewBusinessAndOrStore(receipt);
            if(StringUtils.isNotEmpty(receipt.getId())) {
                fetchedReceipt = receiptManager.findOne(receipt.getId());
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

            bizService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setDocumentStatus(DocumentStatusEnum.TURK_PROCESSED);
            receiptOCR.inActive();

            //Only recheck comments are updated by technician. Receipt notes are never modified
            if(!StringUtils.isEmpty(receiptOCR.getRecheckComment().getText())) {
                CommentEntity comment = receiptOCR.getRecheckComment();
                if(StringUtils.isEmpty(comment.getId())) {
                    comment.setId(null);
                }

                /**
                 * If the comment is not equal then it means Technician has modified the comment and this needs
                 * to be updated with new time. Else do not update the time of recheck comment
                 */
                String fetchedRecheckComment = "";
                if(fetchedReceipt != null && fetchedReceipt.getRecheckComment() != null) {
                    fetchedRecheckComment = fetchedReceipt.getRecheckComment().getText();
                }
                if(!comment.getText().equalsIgnoreCase(fetchedRecheckComment)) {
                    comment.setUpdated();
                    commentManager.save(comment);
                }
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
                messageManager.updateObject(receiptOCR.getId(), DocumentStatusEnum.TURK_REQUEST, DocumentStatusEnum.TURK_PROCESSED);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, DocumentStatusEnum.TURK_PROCESSED, DocumentStatusEnum.TURK_REQUEST);
                throw exce;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(receipt.getTotalString());
            sb.append(" '").append(receipt.getBizName().getName()).append("' ");
            sb.append("receipt re-checked");
            notificationService.addNotification(sb.toString(), NotificationTypeEnum.RECEIPT, receipt);

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

                receiptOCR.setDocumentStatus(DocumentStatusEnum.OCR_PROCESSED);
                receiptOCRManager.save(receiptOCR);
                //log.error("Failed to rollback Receipt OCR: " + receiptOCR.getId() + ", error message: " + e.getLocalizedMessage());

                messageManager.undoUpdateObject(receiptOCR.getId(), false, DocumentStatusEnum.TURK_PROCESSED, DocumentStatusEnum.TURK_REQUEST);
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
            receiptOCR = receiptOCRManager.findOne(receiptOCR.getId());
            receiptOCR.setDocumentStatus(DocumentStatusEnum.TURK_RECEIPT_REJECT);
            receiptOCR.setBizName(null);
            receiptOCR.setBizStore(null);
            receiptOCR.inActive();
            receiptOCR.markAsDeleted();
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), DocumentStatusEnum.OCR_PROCESSED, DocumentStatusEnum.TURK_RECEIPT_REJECT);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, DocumentStatusEnum.TURK_RECEIPT_REJECT, DocumentStatusEnum.OCR_PROCESSED);
                throw exce;
            }
            itemOCRManager.deleteWhereReceipt(receiptOCR);

            fileSystemService.deleteSoft(receiptOCR.getReceiptBlobId());
            fileSystemService.deleteSoft(receiptOCR.getReceiptScaledBlobId());
            storageManager.deleteSoft(receiptOCR.getReceiptBlobId());
            storageManager.deleteSoft(receiptOCR.getReceiptScaledBlobId());
            GridFSDBFile gridFSDBFile = storageManager.get(receiptOCR.getReceiptBlobId().iterator().next().getBlobId());
            DBObject dbObject =  gridFSDBFile.getMetaData();

            StringBuilder sb = new StringBuilder();
            sb.append("Could not process receipt '").append(dbObject.get("ORIGINAL_FILENAME")).append("'");
            notificationService.addNotification(sb.toString(), NotificationTypeEnum.RECEIPT_OCR, receiptOCR);

        } catch(Exception exce) {
            log.error("Rejection of a receipt failed: " + exce.getLocalizedMessage());
            log.warn("Revert all the transaction for ReceiptOCR: " + receiptOCR.getId());

            receiptOCR.setDocumentStatus(DocumentStatusEnum.OCR_PROCESSED);
            receiptOCR.active();
            receiptOCRManager.save(receiptOCR);
            //log.error("Failed to rollback Receipt OCR: " + receiptOCR.getId() + ", error message: " + e.getLocalizedMessage());

            messageManager.undoUpdateObject(receiptOCR.getId(), false, DocumentStatusEnum.TURK_RECEIPT_REJECT, DocumentStatusEnum.OCR_PROCESSED);
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
            storageManager.deleteHard(receiptEntityOCR.getReceiptScaledBlobId());
            fileSystemService.deleteHard(receiptEntityOCR.getReceiptBlobId());
            fileSystemService.deleteHard(receiptEntityOCR.getReceiptScaledBlobId());
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
        if(item.getExpenseTag() != null && item.getExpenseTag().getId() != null) {
            ExpenseTagEntity expenseType = userProfilePreferenceService.getExpenseType(item.getExpenseTag().getId());
            item.setExpenseTag(expenseType);
        }
    }

    /**
     * Condition to check if the record already exists
     *
     * @param checkSum
     * @return
     */
    public boolean checkIfDuplicate(String checkSum) {
        return receiptManager.existCheckSum(checkSum);
    }
}
