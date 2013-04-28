package com.tholix.service;

import java.util.List;

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
import com.tholix.web.AdminLandingController;

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
    @Autowired private AdminLandingController adminLandingController;

    public ReceiptEntityOCR loadReceiptOCRById(String id) {
        return receiptOCRManager.findOne(id);
    }

    public List<ItemEntityOCR> loadItemsOfReceipt(ReceiptEntityOCR receiptEntity) {
        return itemOCRManager.getWhereReceipt(receiptEntity);
    }

    @Transactional(rollbackFor={Exception.class})
    public void turkProcessReceipt(ReceiptEntity receipt, List<ItemEntity> items, ReceiptEntityOCR receiptEntityOCR) throws Exception {
        try {
            adminLandingController.saveNewBusinessAndOrStore(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            adminLandingController.saveNewBusinessAndOrStore(receiptEntityOCR);
            receiptEntityOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
            receiptOCRManager.save(receiptEntityOCR);

            try {
                messageManager.updateObject(receiptEntityOCR.getId());
            } catch(Exception exce) {
                log.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(receiptEntityOCR.getId(), false);
                throw exce;
            }
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
            log.warn("Revert all the transaction for Receipt: " + receipt.getId() + ", ReceiptOCR: " + receiptEntityOCR.getId());

            //For rollback
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

            receiptEntityOCR.setReceiptStatus(ReceiptStatusEnum.OCR_PROCESSED);
            receiptOCRManager.save(receiptEntityOCR);

            messageManager.undoUpdateObject(receiptEntityOCR.getId(), false);
            //End of roll back

            log.info("Complete with rollback: throwing exception");
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
        }
    }
}
