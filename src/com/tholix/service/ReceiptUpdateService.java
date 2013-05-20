package com.tholix.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ReceiptEntityOCR loadReceiptOCRById(String id) {
        return receiptOCRManager.findOne(id);
    }

    public List<ItemEntityOCR> loadItemsOfReceipt(ReceiptEntityOCR receiptEntity) {
        return itemOCRManager.getWhereReceipt(receiptEntity);
    }

    @Transactional(rollbackFor={Exception.class})
    public void turkReceipt(ReceiptEntity receipt, List<ItemEntity> items, ReceiptEntityOCR receiptOCR) throws Exception {
        try {
            adminLandingService.saveNewBusinessAndOrStore(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            adminLandingService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
            receiptOCR.setActive(false);
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

                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.OCR_PROCESSED);
                //End of roll back

                log.info("Complete with rollback: throwing exception");
            }
            throw new Exception(exce.getLocalizedMessage());
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void turkReceiptReCheck(ReceiptEntity receipt, List<ItemEntity> items, ReceiptEntityOCR receiptOCR) throws Exception {
        try {
            adminLandingService.saveNewBusinessAndOrStore(receipt);
            if(StringUtils.isNotEmpty(receipt.getId())) {
                ReceiptEntity fetchedReceipt = receiptManager.findOne(receipt.getId());
                receipt.setVersion(fetchedReceipt.getVersion());
                receipt.setCreated(fetchedReceipt.getCreated());
            }
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            adminLandingService.saveNewBusinessAndOrStore(receiptOCR);
            receiptOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
            receiptOCR.setReceiptId(receipt.getId());
            receiptOCR.inActive();
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

                messageManager.undoUpdateObject(receiptOCR.getId(), false, ReceiptStatusEnum.TURK_PROCESSED, ReceiptStatusEnum.TURK_REQUEST);
                //End of roll back

                log.info("Complete with rollback: throwing exception");
            }
            throw new Exception(exce.getLocalizedMessage());
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

            //Why fetch when its working with just Id?
            //item.setExpenseType(userProfilePreferenceService.getExpenseType(item.getExpenseType().getId()));
        }
    }
}
