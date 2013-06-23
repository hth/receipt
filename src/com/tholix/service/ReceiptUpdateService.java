package com.tholix.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.repository.ItemManager;
import com.tholix.repository.ItemOCRManager;
import com.tholix.repository.MessageManager;
import com.tholix.repository.ReceiptManager;
import com.tholix.repository.ReceiptOCRManager;

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
    @Autowired private ReceiptService receiptService;

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
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            log.warn("Revert all the transaction for Receipt: " + receipt.getId() + ", ReceiptOCR: " + receiptOCR.getId());

            //For rollback
            if(StringUtils.isNotEmpty(receipt.getId())) {
                long sizeReceiptInitial = receiptManager.collectionSize();
                long sizeItemInitial = itemManager.collectionSize();

                itemManager.deleteWhereReceipt(receipt);
                receiptManager.delete(receipt);

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
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            adminLandingService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
            //Why set again when this is already set the first time
            //receiptOCR.setReceiptId(receipt.getId());
            receiptOCR.inActive();
            if(StringUtils.isEmpty(receiptOCR.getComment().getComment())) {
                receiptOCR.setComment(null);
            }
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), ReceiptStatusEnum.TURK_REQUEST, ReceiptStatusEnum.TURK_PROCESSED);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.TURK_REQUEST);
                throw exce;
            }
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            log.warn("Revert all the transaction for Receipt: " + receipt.getId() + ", ReceiptOCR: " + receiptOCR.getId());

            //For rollback
            if(StringUtils.isNotEmpty(receipt.getId())) {
                long sizeReceiptInitial = receiptManager.collectionSize();
                long sizeItemInitial = itemManager.collectionSize();

                itemManager.deleteWhereReceipt(receipt);
                receiptManager.delete(receipt);

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
            receiptOCRManager.save(receiptOCR);

            try {
                messageManager.updateObject(receiptOCR.getId(), ReceiptStatusEnum.OCR_PROCESSED, ReceiptStatusEnum.TURK_RECEIPT_REJECT);
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_RECEIPT_REJECT, ReceiptStatusEnum.OCR_PROCESSED);
                throw exce;
            }
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
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
     *
     * @param receiptOCR
     */
    public void deletePendingReceiptOCR(ReceiptEntityOCR receiptOCR) {
        receiptOCRManager.delete(receiptOCR);
        itemOCRManager.deleteWhereReceipt(receiptOCR);
        messageManager.deleteAllForReceiptOCR(receiptOCR.getId());

        ReceiptEntity receiptEntity = receiptManager.findWithReceiptOCR(receiptOCR.getId());
        if(receiptEntity != null) {
            //At this point ReceiptEntity is inactive because it already exists in the Collection
            if(receiptEntity.isActive()) {
                //Should never go here as it has to be inactive in this process, but any how
                log.error("Invalid condition reached when user tried deleting a pending receipt: " +
                        "Receipt OCR Id: " + receiptOCR.getId() + ", Receipt Id: " + receiptEntity.getId());
            } else {
                log.info("ReceiptOCR has been processed by technician. Delete operation performed after its processed.");
                receiptManager.delete(receiptEntity);
            }
        } else {
            log.info("ReceiptOCR has NOT been processed by technician. Delete operation performed before it could be processed.");
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
