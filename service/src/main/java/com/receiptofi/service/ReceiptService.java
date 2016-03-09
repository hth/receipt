package com.receiptofi.service;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.domain.types.SplitActionEnum;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.service.routes.FileUploadDocumentSenderJMS;
import com.receiptofi.utils.Maths;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    @Autowired private CommentService commentService;
    @Autowired private FileSystemService fileSystemService;
    @Autowired private CloudFileService cloudFileService;
    @Autowired private ExpensesService expensesService;
    @Autowired private NotificationService notificationService;
    @Autowired private FriendService friendService;
    @Autowired private SplitExpensesService splitExpensesService;

    /**
     * Do not use this query unless you are using during split.
     *
     * @param receiptId
     * @return
     */
    public ReceiptEntity findReceipt(String receiptId) {
        return receiptManager.findReceipt(receiptId);
    }

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
     * Find receipt for a receipt id for a specific user profile id.
     *
     * @param receiptId
     * @param receiptUserId
     * @return
     */
    @Mobile
    public ReceiptEntity findReceiptForMobile(String receiptId, String receiptUserId) {
        return receiptManager.findReceiptForMobile(receiptId, receiptUserId);
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

    public long countReceiptsUsingExpenseType(String expenseTypeId, String rid) {
        return receiptManager.countReceiptsUsingExpenseType(expenseTypeId, rid);
    }

    /**
     * Delete a Receipt and its associated data.
     *
     * @param receiptId - Receipt id to delete
     */
    public boolean deleteReceipt(String receiptId, String rid) {
        ReceiptEntity receipt = receiptManager.getReceipt(receiptId, rid);
        if (null == receipt) {
            return false;
        }

        if (receipt.isActive() && StringUtils.isBlank(receipt.getReferReceiptId())) {

            if (splitExpensesService.hasSettleProcessStarted(receiptId)) {
                LOG.info("Receipt delete failed. Middle of transaction as settle process has started for rdid={}", receiptId);
                return false;
            }

            /** Notification message when receipt is deleted. */
            String md = documentUpdateService.getNotificationMessageForReceiptProcess(receipt, "deleted");

            itemManager.deleteSoft(receipt);
            fileSystemService.deleteSoft(receipt.getFileSystemEntities());

            if (null != receipt.getRecheckComment() && !StringUtils.isEmpty(receipt.getRecheckComment().getId())) {
                commentService.deleteHard(receipt.getRecheckComment());
            }
            if (null != receipt.getNotes() && !StringUtils.isEmpty(receipt.getNotes().getId())) {
                commentService.deleteHard(receipt.getNotes());
            }

            if (!StringUtils.isEmpty(receipt.getDocumentId())) {
                DocumentEntity document = documentManager.findDocumentByRid(receipt.getDocumentId(), rid);
                if (null != document) {
                    itemOCRManager.deleteWhereReceipt(document);
                    documentManager.deleteHard(document);
                    receipt.setDocumentId(null);
                }
            }

            receiptManager.deleteSoft(receipt);
            for (FileSystemEntity fileSystem : receipt.getFileSystemEntities()) {
                CloudFileEntity cloudFile = CloudFileEntity.newInstance(fileSystem.getKey());
                cloudFileService.save(cloudFile);
            }

            /** Added document deleted successfully. */
            notificationService.addNotification(
                    md,
                    NotificationTypeEnum.RECEIPT_DELETED,
                    NotificationGroupEnum.R,
                    receipt);
            return true;
        } else if (StringUtils.isNotBlank(receipt.getReferReceiptId())) {
            /**
             * User is deleting their split receipt or shared receipt. This is as good as original owner of the receipt
             * performing a delete operation. If this receipt is a split receipt, then user is removing self from split.
             * This action will also remove all reference to the shared receipt.
             *
             * Delete will be successful only when settle transaction for split has not started. Otherwise nothing will
             * be change.
             */
            return splitAction(rid, SplitActionEnum.R, receiptManager.findReceipt(receipt.getReferReceiptId()));
        } else {
            LOG.error("Attempt to delete inactive Receipt={}, Browser Back Action performed", receipt.getId());
            throw new RuntimeException("Receipt no longer exists");
        }
    }

    public boolean softDeleteFriendReceipt(String receiptId, String rid) {
        return receiptManager.softDeleteFriendReceipt(receiptId, rid);
    }

    /**
     * Inactive the receipt and actives ReceiptOCR. Delete all the ItemOCR and recreate from Items.
     * Then delete all the items.
     *
     * @param receiptId
     * @param rid
     * @throws Exception
     */
    public boolean recheck(String receiptId, String rid) throws Exception {
        try {
            ReceiptEntity receipt = receiptManager.getReceipt(receiptId, rid);

            if (splitExpensesService.hasSettleProcessStarted(receiptId)) {
                LOG.info("Receipt re-check failed. Middle of transaction as settle process has started for rdid={}", receiptId);
                return false;
            }

            if (null == receipt.getDocumentId()) {
                LOG.error("No receiptOCR id found in Receipt={}, aborting the reopen process", receipt.getId());
                throw new RuntimeException("Receipt could not be requested for Re-Check. Contact administrator with Receipt # " + receipt.getId() + ", contact Administrator with the Id");
            } else {
                if (receipt.isActive()) {
                    receipt.inActive();
                    List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());

                    DocumentEntity receiptOCR = documentManager.findDocumentByRid(receipt.getDocumentId(), rid);
                    receiptOCR.active();
                    receiptOCR.setDocumentStatus(DocumentStatusEnum.REPROCESS);
                    receiptOCR.setNotifyUser(false);
                    receiptOCR.setRecheckComment(receipt.getRecheckComment());
                    receiptOCR.setNotes(receipt.getNotes());
                    receiptOCR.setProcessedBy(receipt.getProcessedBy());

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
                    return true;
                } else {
                    LOG.error("Attempt to invoke re-check on Receipt={}, Browser Back Action performed", receipt.getId());
                    throw new RuntimeException("Receipt no longer exists");
                }
            }
        } catch (RuntimeException e) {
            LOG.error("Exception during customer requesting receipt recheck operation, reason={}", e.getLocalizedMessage(), e);

            //Need to send a well formatted error message to customer instead of jumbled mumbled exception stacktrace
            throw new RuntimeException(
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

        items.stream().filter(item -> StringUtils.isNotEmpty(item.getName())).forEach(item -> {
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
        });

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
            commentService.save(commentEntity);
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
            commentService.save(commentEntity);
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
            commentService.save(commentEntity);
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
    public List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String userProfileId, DateTime receiptForMonth) {
        return receiptManager.findReceipt(bizNameEntity, userProfileId, receiptForMonth);
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
     * @param receipt
     * @return
     */
    public boolean updateReceiptWithExpReportFilename(ReceiptEntity receipt) {
        try {
            save(receipt);
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
        Assert.notNull(receipt, "ReceiptEntity should not be null");
        ExpenseTagEntity expenseTag = expensesService.findExpenseTag(receipt.getReceiptUserId(), expenseTagId);
        if (null != expenseTag) {
            receipt.setExpenseTag(expenseTag);
            save(receipt);
            itemService.updateAllItemWithExpenseTag(receipt.getId(), expenseTag.getId());
        } else {
            LOG.error("No such expenseTagId={} found for rid={}", expenseTagId, receipt.getReceiptUserId());
        }

        return expenseTag;
    }

    public List<ReceiptEntity> findAllReceipts(String rid) {
        return receiptManager.findAllReceipts(rid);
    }

    public void save(ReceiptEntity receipt) {
        receiptManager.save(receipt);
    }

    /**
     * Add or Remove friend from split. This is valid when split status is still Un-Settled.
     * If settle process has started. Then receipt would not be able to be modified.
     *
     * @param fid
     * @param splitAction
     * @param receipt
     * @return
     */
    public boolean splitAction(
            String fid,
            SplitActionEnum splitAction,
            ReceiptEntity receipt
    ) {
        boolean result = false;

        String rdid = receipt.getReferReceiptId();
        if (StringUtils.isBlank(rdid)) {
            rdid = receipt.getId();
        }

        if (splitExpensesService.hasSettleProcessStarted(rdid)) {
            LOG.info("Middle of transaction as settle process has started for rdid={} splitAction={}", rdid, splitAction);
            return result;
        }

        switch (splitAction) {
            case A:
                FriendEntity friend = friendService.getConnection(receipt.getReceiptUserId(), fid);
                if (null != friend) {
                    if (!splitExpensesService.doesExists(receipt.getId(), receipt.getReceiptUserId(), fid)) {
                        splitExpensesService.save(new SplitExpensesEntity(fid, receipt));

                        double splitTotal = Maths.divide(receipt.getTotal(), receipt.getSplitCount() + 1).doubleValue();
                        double splitTax = Maths.divide(receipt.getTax(), receipt.getSplitCount() + 1).doubleValue();

                        if (receiptManager.increaseSplitCount(receipt.getId(), splitTotal, splitTax)) {

                            /** Refresh receipt. */
                            ReceiptEntity receiptRefreshed = receiptManager.findReceipt(receipt.getId());

                            /** Update all existing friend receipt. */
                            updateFriendReceipt(receiptRefreshed);

                            /** Create new receipt for friend or update existing receipt for friend. */
                            ReceiptEntity friendReceipt = receiptManager.findOne(fid, receiptRefreshed.getReceiptDate(), receiptRefreshed.getTotal());
                            if (friendReceipt == null) {
                                friendReceipt = receiptRefreshed.createReceiptForFriend(fid);
                                save(friendReceipt);
                            } else {
                                ReceiptEntity copyOfReceipt = receiptRefreshed.createReceiptForFriend(fid);
                                copyOfReceipt.setId(friendReceipt.getId());
                                copyOfReceipt.setVersion(friendReceipt.getVersion());
                                save(copyOfReceipt);
                            }
                        } else {
                            LOG.error("Failed to split={} fid={} rid={}", splitAction, fid, receipt.getReceiptUserId());
                        }
                    } else {
                        LOG.warn("Already split expenses with fid={} rid={} skipping split", fid, receipt.getReceiptUserId());
                    }
                    result = true;
                } else {
                    LOG.error("No friend connection found fid={} rid={} skipping split", fid, receipt.getReceiptUserId());
                }
                break;
            case R:
                if (splitExpensesService.deleteHard(receipt.getId(), receipt.getReceiptUserId(), fid)) {

                    if (receipt.getSplitCount() > 1) {
                        double splitTotal = Maths.divide(receipt.getTotal(), receipt.getSplitCount() - 1).doubleValue();
                        double splitTax = Maths.divide(receipt.getTax(), receipt.getSplitCount() - 1).doubleValue();

                        if (receiptManager.decreaseSplitCount(receipt.getId(), splitTotal, splitTax)) {

                            /** Refresh receipt. */
                            ReceiptEntity receiptRefreshed = receiptManager.findReceipt(receipt.getId());

                            /** Update all existing friend receipt. */
                            updateFriendReceipt(receiptRefreshed);

                            /** Remove entry. */
                            softDeleteFriendReceipt(receiptRefreshed.getId(), fid);
                            result = true;
                        } else {
                            LOG.error("Failed to split={} fid={} rid={}", splitAction, fid, receipt.getReceiptUserId());
                        }
                    } else {
                        LOG.error("Split expenses count going below 1 rid={} id={}", receipt.getReceiptUserId(), receipt.getId());
                    }
                } else {
                    LOG.warn("Not found split expenses between fid={} rid={} OR SplitStatus is not Unsettled. Skipping.",
                            fid, receipt.getReceiptUserId());
                }

                break;
        }
        return result;
    }

    /**
     * Update all existing friend receipt.
     *
     * @param receipt
     */
    private void updateFriendReceipt(ReceiptEntity receipt) {
        if (receipt.getSplitCount() > 1) {
            receiptManager.updateFriendReceipt(
                    receipt.getId(),
                    receipt.getSplitCount(),
                    receipt.getSplitTotal(),
                    receipt.getSplitTax());

            if (splitExpensesService.updateSplitTotal(receipt.getId(), receipt.getSplitTotal())) {
                LOG.debug("Success update split total with new price");
            } else {
                LOG.debug("Failure update split total with new price");
            }
        }
    }
}
