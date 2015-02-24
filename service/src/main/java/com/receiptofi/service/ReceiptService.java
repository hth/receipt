package com.receiptofi.service;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.repository.CommentManager;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 1:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ReceiptService {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptService.class);

    @Autowired private ReceiptManager receiptManager;
    @Autowired private DocumentManager documentManager;
    @Autowired private DocumentUpdateService documentUpdateService;
    @Autowired private ItemManager itemManager;
    @Autowired private ItemService itemService;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private UserProfileManager userProfileManager;
    @Autowired private FileUploadDocumentSenderJMS senderJMS;
    @Autowired private CommentManager commentManager;
    @Autowired private FileSystemService fileSystemService;
    @Autowired private CloudFileService cloudFileService;
    @Autowired private ExpensesService expensesService;

    /**
     * Find receipt for a receipt id for a specific user profile id.
     *
     * @param receiptId
     * @param receiptUserId
     * @return
     */
    public ReceiptEntity findReceipt(String receiptId, String receiptUserId) {
        return receiptManager.findReceipt(receiptId, receiptUserId);
    }

    /**
     * @param dateTime
     * @param receiptUserId
     * @return
     */
    public List<ReceiptEntity> findReceipt(DateTime dateTime, String receiptUserId) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();

        return receiptManager.findThisDayReceipts(year, month, day, receiptUserId);
    }

    /**
     * Delete a Receipt and its associated data.
     *
     * @param receiptId - Receipt id to delete
     */
    public boolean deleteReceipt(String receiptId, String receiptUserId) throws Exception {
        ReceiptEntity receipt = receiptManager.findOne(receiptId, receiptUserId);
        if (null == receipt) {
            return false;
        }
        if (receipt.isActive()) {
            itemManager.deleteSoft(receipt);
            fileSystemService.deleteSoft(receipt.getFileSystemEntities());

            if (receipt.getRecheckComment() != null && !StringUtils.isEmpty(receipt.getRecheckComment().getId())) {
                commentManager.deleteHard(receipt.getRecheckComment());
            }
            if (receipt.getNotes() != null && !StringUtils.isEmpty(receipt.getNotes().getId())) {
                commentManager.deleteHard(receipt.getNotes());
            }

            if (!StringUtils.isEmpty(receipt.getDocumentId())) {
                DocumentEntity documentEntity = documentManager.findOne(receipt.getDocumentId(), receiptUserId);
                if (documentEntity != null) {
                    itemOCRManager.deleteWhereReceipt(documentEntity);
                    documentManager.deleteHard(documentEntity);
                    receipt.setDocumentId(null);
                }
            }

            receiptManager.deleteSoft(receipt);
            for (FileSystemEntity fileSystem : receipt.getFileSystemEntities()) {
                CloudFileEntity cloudFile = CloudFileEntity.newInstance(fileSystem.getKey());
                cloudFileService.save(cloudFile);
            }
            return true;
        } else {
            LOG.error("Attempt to delete inactive Receipt={}, Browser Back Action performed", receipt.getId());
            throw new Exception("Receipt no longer exists");
        }
    }

    /**
     * Inactive the receipt and active ReceiptOCR. Delete all the ItemOCR and recreate from Items.
     * Then delete all the items.
     *
     * @param receiptId
     * @param receiptUserId
     * @throws Exception
     */
    public void reopen(String receiptId, String receiptUserId) throws Exception {
        try {
            ReceiptEntity receipt = receiptManager.findOne(receiptId, receiptUserId);
            if (null == receipt.getDocumentId()) {
                LOG.error("No receiptOCR id found in Receipt={}, aborting the reopen process", receipt.getId());
                throw new Exception("Receipt could not be requested for Re-Check. Contact administrator with Receipt # " + receipt.getId() + ", contact Administrator with the Id");
            } else {
                if (receipt.isActive()) {
                    receipt.inActive();
                    List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());

                    DocumentEntity receiptOCR = documentManager.findOne(receipt.getDocumentId(), receiptUserId);
                    receiptOCR.active();
                    receiptOCR.setDocumentStatus(DocumentStatusEnum.REPROCESS);
                    receiptOCR.setRecheckComment(receipt.getRecheckComment());
                    receiptOCR.setNotes(receipt.getNotes());

                    /** All activity at the end is better because you never know what could go wrong during populating other data */
                    receipt.setReceiptStatus(DocumentStatusEnum.REPROCESS);
                    receiptManager.save(receipt);
                    documentManager.save(receiptOCR);
                    itemOCRManager.deleteWhereReceipt(receiptOCR);

                    List<ItemEntityOCR> ocrItems = getItemEntityFromItemEntityOCR(items, receiptOCR);
                    itemOCRManager.saveObjects(ocrItems);
                    itemManager.deleteWhereReceipt(receipt);

                    LOG.info("DocumentEntity @Id after save: " + receiptOCR.getId());
                    UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(receiptOCR.getReceiptUserId());
                    senderJMS.send(receiptOCR, userProfile);
                } else {
                    LOG.error("Attempt to invoke re-check on Receipt={}, Browser Back Action performed", receipt.getId());
                    throw new Exception("Receipt no longer exists");
                }
            }
        } catch (Exception e) {
            LOG.error("Exception during customer requesting receipt recheck operation, reason={}", e.getLocalizedMessage(), e);

            //Need to send a well formatted error message to customer instead of jumbled mumbled exception stacktrace
            throw new Exception(
                    "Exception occurred during requesting receipt recheck operation for Receipt # " +
                            receiptId +
                            ", contact Administrator with the Id"
            );
        }
    }


    /**
     * Used when data is read from Receipt and Item Entity during re-check process.
     *
     * @param items
     * @param document
     * @return
     */
    public List<ItemEntityOCR> getItemEntityFromItemEntityOCR(List<ItemEntity> items, DocumentEntity document) {
        List<ItemEntityOCR> listOfItems = new ArrayList<>();

        for (ItemEntity item : items) {
            if (StringUtils.isNotEmpty(item.getName())) {
                ItemEntityOCR itemOCR = ItemEntityOCR.newInstance();
                itemOCR.setName(item.getName());
                itemOCR.setPrice(item.getPrice().toString());
                itemOCR.setTaxed(item.getTaxed());
                itemOCR.setSequence(item.getSequence());
                itemOCR.setDocument(document);
                itemOCR.setReceiptUserId(document.getReceiptUserId());
                itemOCR.setExpenseTag(item.getExpenseTag());
                itemOCR.setCreated(item.getCreated());
                itemOCR.setQuantity(item.getQuantity());
                itemOCR.setUpdated();

                itemOCR.setBizName(document.getBizName());
                listOfItems.add(itemOCR);
            }
        }

        return listOfItems;
    }

    /**
     * Saves notes to receipt.
     *
     * @param notes
     * @param receiptId
     * @param userProfileId
     * @return
     */
    public boolean updateReceiptNotes(String notes, String receiptId, String userProfileId) {
        ReceiptEntity receiptEntity = receiptManager.findReceipt(receiptId, userProfileId);
        CommentEntity commentEntity = receiptEntity.getNotes();
        boolean commentEntityBoolean = false;
        if (null == commentEntity) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(CommentTypeEnum.NOTES);
            commentEntity.setText(notes);
        } else {
            commentEntity.setText(notes);
        }
        try {
            commentEntity.setUpdated();
            commentManager.save(commentEntity);
            if (commentEntityBoolean) {
                receiptEntity.setNotes(commentEntity);
                receiptManager.save(receiptEntity);
            }
            return true;
        } catch (Exception exce) {
            LOG.error("Failed updating notes for Receipt={}, reason={}", receiptId, exce.getLocalizedMessage(), exce);
            return false;
        }
    }

    /**
     * Saves recheck comment to receipt.
     *
     * @param comment
     * @param receiptId
     * @param userProfileId
     * @return
     */
    public boolean updateReceiptComment(String comment, String receiptId, String userProfileId) {
        ReceiptEntity receiptEntity = receiptManager.findReceipt(receiptId, userProfileId);
        CommentEntity commentEntity = receiptEntity.getRecheckComment();
        boolean commentEntityBoolean = false;
        if (null == commentEntity) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(CommentTypeEnum.RECHECK);
            commentEntity.setText(comment);
        } else {
            commentEntity.setText(comment);
        }
        try {
            commentEntity.setUpdated();
            commentManager.save(commentEntity);
            if (commentEntityBoolean) {
                receiptEntity.setRecheckComment(commentEntity);
                receiptManager.save(receiptEntity);
            }
            return true;
        } catch (Exception exce) {
            LOG.error("Failed updating comment for Receipt={}, reason={}", receiptId, exce.getLocalizedMessage(), exce);
            return false;
        }
    }

    /**
     * Saves recheck comment to Document.
     *
     * @param comment
     * @param documentId
     * @return
     */
    public boolean updateDocumentComment(String comment, String documentId) {
        DocumentEntity documentEntity = documentUpdateService.loadActiveDocumentById(documentId);
        CommentEntity commentEntity = documentEntity.getRecheckComment();
        boolean commentEntityBoolean = false;
        if (null == commentEntity) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(CommentTypeEnum.RECHECK);
            commentEntity.setText(comment);
        } else {
            commentEntity.setText(comment);
        }
        try {
            commentEntity.setUpdated();
            commentManager.save(commentEntity);
            if (commentEntityBoolean) {
                documentEntity.setRecheckComment(commentEntity);
                documentManager.save(documentEntity);
            }
            return true;
        } catch (Exception exce) {
            LOG.error("Failed updating comment for ReceiptOCR={}, reason={}", documentId, exce.getLocalizedMessage(), exce);
            return false;
        }
    }

    /**
     * @param bizNameEntity
     * @param userProfileId
     * @return
     */
    public List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String userProfileId) {
        return receiptManager.findReceipt(bizNameEntity, userProfileId);
    }

    /**
     * Counts all the valid and invalid receipt that has referred the store.
     *
     * @param bizStoreEntity
     * @return
     */
    public long countAllReceiptForAStore(BizStoreEntity bizStoreEntity) {
        return receiptManager.countAllReceiptForAStore(bizStoreEntity);
    }

    /**
     * Counts all the valid and invalid receipt that has referred the biz name.
     *
     * @param bizNameEntity
     * @return
     */
    public long countAllReceiptForABizName(BizNameEntity bizNameEntity) {
        return receiptManager.countAllReceiptForABizName(bizNameEntity);
    }

    /**
     * Used for updating expense report info in the receipt.
     *
     * @param receiptEntity
     * @return
     */
    public boolean updateReceiptWithExpReportFilename(ReceiptEntity receiptEntity) {
        try {
            receiptManager.save(receiptEntity);
        } catch (Exception e) {
            LOG.error("Failed updating ReceiptEntity with Expense Report Filename, reason={}", e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    public void removeExpensofiFilenameReference(String filename) {
        receiptManager.removeExpensofiFilenameReference(filename);
    }

    /**
     * Updates expense tag of receipt and updates the same for all the items.
     *
     * @param receipt
     * @param expenseTagId
     * @return
     */
    public ExpenseTagEntity updateReceiptExpenseTag(ReceiptEntity receipt, String expenseTagId) {
        ExpenseTagEntity expenseTag = null;
        if (null != receipt) {
            expenseTag = expensesService.findExpenseTag(expenseTagId);
            receipt.setExpenseTag(expenseTag);
            receiptManager.save(receipt);
            itemService.updateAllItemWithExpenseTag(receipt.getId(), expenseTag.getId());
        }

        return expenseTag;
    }
}
