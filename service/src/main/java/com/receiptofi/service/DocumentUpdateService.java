package com.receiptofi.service;

import static com.receiptofi.domain.types.DocumentStatusEnum.PENDING;
import static com.receiptofi.domain.types.DocumentStatusEnum.PROCESSED;
import static com.receiptofi.domain.types.DocumentStatusEnum.REJECT;
import static com.receiptofi.domain.types.DocumentStatusEnum.REPROCESS;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.PaymentCardEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.CardNetworkEnum;
import com.receiptofi.domain.types.DocumentOfTypeEnum;
import com.receiptofi.domain.types.DocumentRejectReasonEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.MessageDocumentManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 2:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class DocumentUpdateService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentUpdateService.class);

    private DocumentService documentService;
    private ItemOCRManager itemOCRManager;
    private ReceiptManager receiptManager;
    private ItemManager itemManager;
    private MessageDocumentManager messageDocumentManager;
    private BizService bizService;
    private UserProfilePreferenceService userProfilePreferenceService;
    private CommentService commentService;
    private NotificationService notificationService;
    private StorageManager storageManager;
    private FileSystemService fileSystemService;
    private BillingService billingService;
    private ExpensesService expensesService;
    private PaymentCardService paymentCardService;

    @Autowired
    public DocumentUpdateService(
            DocumentService documentService,
            ItemOCRManager itemOCRManager,
            ReceiptManager receiptManager,
            ItemManager itemManager,
            MessageDocumentManager messageDocumentManager,
            BizService bizService,
            UserProfilePreferenceService userProfilePreferenceService,
            CommentService commentService,
            NotificationService notificationService,
            StorageManager storageManager,
            FileSystemService fileSystemService,
            BillingService billingService,
            ExpensesService expensesService,
            PaymentCardService paymentCardService) {

        this.documentService = documentService;
        this.itemOCRManager = itemOCRManager;
        this.receiptManager = receiptManager;
        this.itemManager = itemManager;
        this.messageDocumentManager = messageDocumentManager;
        this.bizService = bizService;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.storageManager = storageManager;
        this.fileSystemService = fileSystemService;
        this.billingService = billingService;
        this.expensesService = expensesService;
        this.paymentCardService = paymentCardService;
    }

    /**
     * This method is used when technician saves the processed receipt for the first time.
     *
     * @param receipt
     * @param items
     * @param document
     */
    public void processDocumentForReceipt(
            String technicianId,
            ReceiptEntity receipt,
            List<ItemEntity> items,
            DocumentEntity document
    ) {
        try {
            Date transaction = new Date();
            DocumentEntity documentEntity = documentService.loadActiveDocumentById(document.getId());
            updateFileSystemEntityWithVersion(receipt, document, documentEntity);

            bizService.saveNewBusinessAndOrStore(receipt);
            if (null != receipt.getBizStore() && StringUtils.isNotBlank(receipt.getBizStore().getCountryShortName())) {
                receipt.setCountryShortName(receipt.getBizStore().getCountryShortName());
            }

            addCardDetailsIfAny(receipt, document);
            receipt.addProcessedBy(transaction, technicianId);
            billingService.updateReceiptWithBillingHistory(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.copyBizNameAndBizStoreFromReceipt(document, receipt);
            document.setDocumentStatus(PROCESSED);
            document.setNotifyUser(false);
            document.setReferenceDocumentId(receipt.getId());
            document.inActive();
            document.addProcessedBy(transaction, technicianId);
            documentService.save(document);

            updateMessageManager(document, PENDING, PROCESSED);

            notificationService.addNotification(
                    notificationService.getNotificationMessageForReceiptProcess(receipt, "processed"),
                    NotificationTypeEnum.RECEIPT,
                    NotificationGroupEnum.R,
                    receipt);

        } catch (Exception exce) {
            LOG.error("Failed processing for Receipt={}, ReceiptOCR={}, reason={}",
                    receipt.getId(), document.getId(), exce.getLocalizedMessage(), exce);

            //For rollback
            if (StringUtils.isNotEmpty(receipt.getId())) {
                LOG.error("Rolling back Receipt={}", receipt.getId());
                long sizeReceiptInitial = receiptManager.collectionSize();
                long sizeItemInitial = itemManager.collectionSize();

                itemManager.deleteWhereReceipt(receipt);
                receiptManager.deleteHard(receipt);

                long sizeReceiptFinal = receiptManager.collectionSize();
                long sizeItemFinal = itemManager.collectionSize();
                if (sizeReceiptInitial == sizeReceiptFinal) {
                    LOG.warn("Initial receipt size and Final receipt size are same={}:{}",
                            sizeReceiptInitial, sizeReceiptFinal);
                } else {
                    LOG.warn("Initial receipt size={}, Final receipt size={}. Removed Receipt={}",
                            sizeReceiptInitial, sizeReceiptFinal, receipt.getId());
                }

                if (sizeItemInitial == sizeItemFinal) {
                    LOG.warn("Initial item size and Final item size are same={}:{}", sizeItemInitial, sizeItemFinal);
                } else {
                    LOG.warn("Initial item size={}, Final item size={}", sizeItemInitial, sizeItemFinal);
                }

                document.setDocumentStatus(PENDING);
                document.setNotifyUser(true);
                documentService.save(document);
                //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

                messageDocumentManager.undoUpdateObject(document.getId(), false, PROCESSED, PENDING);
                //End of roll back

                LOG.warn("Rollback complete for processing document");
            }
            throw new RuntimeException("Failed processing document " + exce);
        }
    }

    /**
     * This method is executed when Technician is re-checking the receipt.
     *
     * @param receipt
     * @param items
     * @param document
     * @throws Exception
     */
    public void processDocumentReceiptReCheck(
            String technicianId,
            ReceiptEntity receipt,
            List<ItemEntity> items,
            DocumentEntity document
    ) {
        ReceiptEntity fetchedReceipt = null;
        try {
            Date transaction = new Date();
            DocumentEntity documentEntity = documentService.loadActiveDocumentById(document.getId());
            updateFileSystemEntityWithVersion(receipt, document, documentEntity);

            bizService.saveNewBusinessAndOrStore(receipt);
            if (null != receipt.getBizStore() && StringUtils.isNotBlank(receipt.getBizStore().getCountryShortName())) {
                receipt.setCountryShortName(receipt.getBizStore().getCountryShortName());
            }

            if (StringUtils.isNotEmpty(receipt.getId())) {
                fetchedReceipt = receiptManager.findReceiptWhileRecheck(receipt.getId(), document.getReceiptUserId());
                if (null == fetchedReceipt) {
                    // By creating new receipt with old id, we move the pending receipt from the list back to users account
                    LOG.error("** Something gone wrong with original Receipt={}, creating another with old receipt id",
                            receipt.getId());
                } else {
                    receipt.setVersion(fetchedReceipt.getVersion());
                    receipt.setCreated(fetchedReceipt.getCreated());
                    receipt.setProcessedBy(fetchedReceipt.getProcessedBy());
                }
            }

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.copyBizNameAndBizStoreFromReceipt(document, receipt);
            document.setDocumentStatus(PROCESSED);
            document.setNotifyUser(false);
            document.inActive();

            //Only recheck comments are updated by technician. Receipt notes are never modified
            if (StringUtils.isEmpty(document.getRecheckComment().getText())) {
                CommentEntity comment = document.getRecheckComment();
                commentService.deleteHard(comment);
                document.setRecheckComment(null);
                receipt.setRecheckComment(null);
            } else {
                CommentEntity comment = document.getRecheckComment();
                if (StringUtils.isEmpty(comment.getId())) {
                    comment.setId(null);
                }

                /**
                 * If the comment is not equal then it means Technician has modified the comment and this needs
                 * to be updated with new time. Else do not update the time of recheck comment.
                 */
                String fetchedRecheckComment = "";
                if (null != fetchedReceipt && null != fetchedReceipt.getRecheckComment()) {
                    fetchedRecheckComment = fetchedReceipt.getRecheckComment().getText();
                }
                if (!comment.getText().equalsIgnoreCase(fetchedRecheckComment)) {
                    comment.setUpdated();
                    commentService.save(comment);
                }
                document.setRecheckComment(comment);
                receipt.setRecheckComment(comment);
            }

            /**
             * Since Technician cannot change notes at least we gotta make sure we are not adding one when the Id for
             * notes are missing.
             */
            if (StringUtils.isEmpty(document.getNotes().getId())) {
                document.setNotes(null);
                receipt.setNotes(null);
            }

            addCardDetailsIfAny(receipt, document);
            receipt.addProcessedBy(transaction, technicianId);
            billingService.updateReceiptWithBillingHistory(receipt);
            receiptManager.save(receipt);

            document.setProcessedBy(documentEntity.getProcessedBy());
            document.addProcessedBy(transaction, technicianId);
            documentService.save(document);

            updateMessageManager(document, REPROCESS, PROCESSED);

            notificationService.addNotification(
                    notificationService.getNotificationMessageForReceiptProcess(receipt, "re-checked"),
                    NotificationTypeEnum.RECEIPT,
                    NotificationGroupEnum.R,
                    receipt);

        } catch (Exception exce) {
            LOG.error("Failed processing for Receipt={}, ReceiptOCR={}, reason={}",
                    receipt.getId(), document.getId(), exce.getLocalizedMessage(), exce);

            //For rollback
            if (StringUtils.isNotEmpty(receipt.getId())) {
                LOG.error("Rolling back Receipt={}", receipt.getId());
                long sizeReceiptInitial = receiptManager.collectionSize();
                long sizeItemInitial = itemManager.collectionSize();

                itemManager.deleteWhereReceipt(receipt);
                receiptManager.deleteHard(receipt);

                long sizeReceiptFinal = receiptManager.collectionSize();
                long sizeItemFinal = itemManager.collectionSize();
                if (sizeReceiptInitial == sizeReceiptFinal) {
                    LOG.warn("Initial receipt size and Final receipt size are same={}:{}",
                            sizeReceiptInitial, sizeReceiptFinal);
                } else {
                    LOG.warn("Initial receipt size={}, Final receipt size={}. Removed Receipt={}",
                            sizeReceiptInitial, sizeReceiptFinal, receipt.getId());
                }

                if (sizeItemInitial == sizeItemFinal) {
                    LOG.warn("Initial item size and Final item size are same={}:{}", sizeItemInitial, sizeItemFinal);
                } else {
                    LOG.warn("Initial item size={}, Final item size={}", sizeItemInitial, sizeItemFinal);
                }

                document.setDocumentStatus(PENDING);
                document.setNotifyUser(true);
                documentService.save(document);
                //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

                messageDocumentManager.undoUpdateObject(document.getId(), false, PROCESSED, REPROCESS);
                //End of roll back

                LOG.warn("Rollback complete for re-processing document");
            }
            throw new RuntimeException("Failed re-processing document " + exce);
        }
    }

    private void addCardDetailsIfAny(ReceiptEntity receipt, DocumentEntity document) {
        if (StringUtils.isNotBlank(document.getCardDigit()) && document.getCardNetwork() != CardNetworkEnum.U) {
            PaymentCardEntity paymentCard = paymentCardService.findCard(receipt.getReceiptUserId(), document.getCardDigit());
            if (null == paymentCard) {
                paymentCard = PaymentCardEntity.newInstance(
                        receipt.getReceiptUserId(),
                        document.getCardNetwork(),
                        document.getCardDigit(),
                        receipt.getReceiptDate());

                paymentCardService.save(paymentCard);
            }
            paymentCardService.updateLastUsed(receipt.getReceiptUserId(), paymentCard.getCardDigit(), receipt.getReceiptDate());
            receipt.setPaymentCard(paymentCard);
        }
    }

    private void updateMessageManager(DocumentEntity receiptOCR, DocumentStatusEnum from, DocumentStatusEnum to) {
        try {
            messageDocumentManager.updateObject(receiptOCR.getId(), from, to);
        } catch (Exception exce) {
            LOG.error(exce.getLocalizedMessage());
            messageDocumentManager.undoUpdateObject(receiptOCR.getId(), false, to, from);
            throw exce;
        }
    }

    /**
     * Reject receipt when invalid or un-readable.
     *
     * @param technicianId
     * @param documentId
     * @param documentOfType
     */
    public void processDocumentForReject(
            String technicianId,
            String documentId,
            DocumentOfTypeEnum documentOfType,
            DocumentRejectReasonEnum documentRejectReason
    ) {
        DocumentEntity document = documentService.loadActiveDocumentById(documentId);
        if (null == document) {
            LOG.warn("Rejected inactive or not found document id={} technicianId={}", documentId, technicianId);
        } else {
            try {
                document.setDocumentStatus(REJECT);
                document.setDocumentRejectReason(documentRejectReason);
                document.setNotifyUser(false);
                document.setDocumentOfType(documentOfType);
                document.setBizName(null);
                document.setBizStore(null);
                document.inActive();
                document.addProcessedBy(new Date(), technicianId);
                document.markAsDeleted();
                documentService.save(document);

                /** Modify MessageDocumentEntity to reject for removing from pending list. */
                updateMessageWithDocumentChanges(document);
                itemOCRManager.deleteWhereReceipt(document);

                fileSystemService.deleteSoft(document.getFileSystemEntities(), FileTypeEnum.D);
                storageManager.deleteSoft(document.getFileSystemEntities());
                GridFSDBFile gridFSDBFile = storageManager.get(document.getFileSystemEntities().iterator().next().getBlobId());
                DBObject dbObject = gridFSDBFile.getMetaData();

                notificationService.addNotification(
                        getNotificationMessageForReceiptReject(dbObject, document.getDocumentRejectReason()),
                        NotificationTypeEnum.DOCUMENT_REJECTED,
                        NotificationGroupEnum.R,
                        document);
            } catch (Exception exce) {
                LOG.error("Revert all the transaction for documentId={}. Rejection of a receipt failed, reason={}",
                        document.getId(), exce.getLocalizedMessage(), exce);

                document.setDocumentStatus(PENDING);
                document.setNotifyUser(true);
                document.active();
                documentService.save(document);
                //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

                messageDocumentManager.undoUpdateObject(document.getId(), false, REJECT, PENDING);
                //End of roll back

                LOG.warn("Rollback complete for rejecting document");
            }
        }
    }

    private void updateMessageWithDocumentChanges(DocumentEntity document) {
        try {
            messageDocumentManager.updateObject(document.getId(), PENDING, REJECT);
        } catch (Exception exce) {
            LOG.error(exce.getLocalizedMessage(), exce);
            messageDocumentManager.undoUpdateObject(document.getId(), false, REJECT, PENDING);
            throw exce;
        }
    }

    /**
     * Delete all the associated data with Document like Item OCR, and Message Receipt Entity OCR including
     * deletion of with Document.
     * But cannot delete Document when the receipt has been processed once and now it pending for re-check.
     *
     * @param document
     */
    public void deletePendingDocument(DocumentEntity document) {
        DocumentEntity documentEntity = documentService.loadActiveDocumentById(document.getId());
        if (null == documentEntity || !StringUtils.isEmpty(documentEntity.getReferenceDocumentId())) {
            LOG.warn("User trying to delete processed Document={}, Receipt={}",
                    document.getId(), document.getReferenceDocumentId());
        } else {
            deleteReceiptOCR(documentEntity);
        }
    }

    /**
     * Delete all the associated data with Document like Item OCR, and Message Receipt Entity OCR including
     * deletion of with Document.
     * But cannot delete Document when the receipt has been processed once and now it pending for re-check.
     *
     * @param document
     */
    public void deleteRejectedDocument(DocumentEntity document) {
        DocumentEntity documentEntity = documentService.loadRejectedDocumentById(document.getId());
        if (null == documentEntity || !StringUtils.isEmpty(documentEntity.getReferenceDocumentId())) {
            LOG.warn("User trying to delete processed Document={}, Receipt={}",
                    document.getId(), document.getReferenceDocumentId());
        } else {
            deleteReceiptOCR(documentEntity);
        }
    }

    private void deleteReceiptOCR(DocumentEntity document) {
        String fileDeleteMessage = document.getFileSystemEntities().stream()
                .map(FileSystemEntity::getOriginalFilename)
                .collect(Collectors.joining(", ")) + " deleted";

        documentService.deleteHard(document);
        itemOCRManager.deleteWhereReceipt(document);
        messageDocumentManager.deleteAllForReceiptOCR(document.getId());
        storageManager.deleteHard(document.getFileSystemEntities());
        fileSystemService.deleteHard(document.getFileSystemEntities());

        /** Added document deleted successfully. */
        notificationService.addNotification(
                fileDeleteMessage,
                NotificationTypeEnum.DOCUMENT_DELETED,
                NotificationGroupEnum.F,
                document);
    }

    /**
     * Populates items with BizNameEntity
     *
     * @param items
     * @param receipt
     */
    private void populateItemsWithBizName(List<ItemEntity> items, ReceiptEntity receipt) {
        for (ItemEntity item : items) {
            item.setBizName(receipt.getBizName());
            populateWithExpenseTag(item);
        }
    }

    /**
     * when Items are populated with just an Id of the expenseType. This normally happens during Re-Check condition.
     * The following code makes sures objects are populated with just not id but with complete object instead.
     * //TODO(hth) in future keep an eye on this object as during save of an ItemEntity the @DBRef expenseType is
     * //TODO(hth) saved as Id instead of an object. As of now it is saved and updated
     *
     * @param item
     */
    private void populateWithExpenseTag(ItemEntity item) {
        if (null != item.getExpenseTag() && null != item.getExpenseTag().getId()) {
            ExpenseTagEntity expenseType = expensesService.getExpenseTag(item.getReceiptUserId(), item.getExpenseTag().getId());
            item.setExpenseTag(expenseType);
        }
    }

    /**
     * Condition to check if the record already exists.
     *
     * @param checkSum
     * @return
     */
    public boolean checkIfDuplicate(String checkSum, String id) {
        return receiptManager.notDeletedChecksumDuplicate(checkSum, id);
    }

    /**
     * Does a similar receipt exists with this checksum. This can be used when adding new receipt or while soft deleting
     * of a receipt.
     *
     * @param checksum
     * @return
     */
    public boolean hasReceiptWithSimilarChecksum(String checksum) {
        return receiptManager.hasRecordWithSimilarChecksum(checksum);
    }

    /**
     * Copies fileSystemEntity from existing documentEntity to receipt and document.
     *
     * @param receipt
     * @param document
     * @param documentEntity
     */
    private void updateFileSystemEntityWithVersion(
            ReceiptEntity receipt,
            DocumentEntity document,
            DocumentEntity documentEntity
    ) {
        receipt.setFileSystemEntities(documentEntity.getFileSystemEntities());
        document.setFileSystemEntities(documentEntity.getFileSystemEntities());
        updateDocumentVersion(document, documentEntity);
    }

    private void updateDocumentVersion(DocumentEntity document, DocumentEntity documentEntity) {
        /** Update the version number as the value could have changed by rotating receipt image through ajax. */
        document.setVersion(documentEntity.getVersion());
    }

    /**
     * Get details for processed by rid.
     *
     * @param processedBy
     * @return
     */
    public Map<Date, UserProfileEntity> getProcessedByUserName(Map<Date, String> processedBy) {
        Map<Date, UserProfileEntity> processedByUser = new LinkedHashMap<>();
        for (Date date : processedBy.keySet()) {
            processedByUser.put(date, userProfilePreferenceService.findByReceiptUserId(processedBy.get(date)));
        }
        return processedByUser;
    }

    private String getNotificationMessageForReceiptReject(DBObject dbObject, DocumentRejectReasonEnum documentRejectReason) {
        return getNotificationMessageForReceiptReject(dbObject.get("ORIGINAL_FILENAME").toString(), documentRejectReason);
    }

    private String getNotificationMessageForReceiptReject(String originalFilename, DocumentRejectReasonEnum documentRejectReason) {
        switch (documentRejectReason) {
            case C:
            case D:
            case E:
            case M:
            case V:
            case T:
            case O:
                return "Document '" + originalFilename + "' " + documentRejectReason.getInSentence();
            default:
                LOG.error("Document Reject Reason is not defined");
                throw new IllegalStateException("Document Reject Reason is not defined");
        }
    }
}
