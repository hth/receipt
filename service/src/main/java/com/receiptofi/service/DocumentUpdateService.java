package com.receiptofi.service;

import static com.receiptofi.domain.types.DocumentStatusEnum.*;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.DocumentOfTypeEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.NotificationTypeEnum;
import com.receiptofi.repository.CommentManager;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ItemOCRManager;
import com.receiptofi.repository.MessageManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 2:09 PM
 */
@Service
public final class DocumentUpdateService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentUpdateService.class);

    @Autowired private DocumentManager documentManager;
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
    @Autowired private MileageService mileageService;

    public DocumentEntity loadActiveDocumentById(String id) {
        return documentManager.findActiveOne(id);
    }

    public DocumentEntity loadRejectedDocumentById(String id) {
        return documentManager.findRejectedOne(id);
    }

    public DocumentEntity findOne(String documentId, String userProfileId) {
        return documentManager.findOne(documentId, userProfileId);
    }

    public List<ItemEntityOCR> loadItemsOfReceipt(DocumentEntity receiptEntity) {
        return itemOCRManager.getWhereReceipt(receiptEntity);
    }

    /**
     * This method is used when technician saves the processed receipt for the first time.
     *
     * @param receipt
     * @param items
     * @param documentForm
     * @throws Exception
     */
    public void turkProcessReceipt(ReceiptEntity receipt, List<ItemEntity> items, DocumentEntity documentForm) {
        try {
            DocumentEntity documentEntity = loadActiveDocumentById(documentForm.getId());

            receipt.setFileSystemEntities(documentEntity.getFileSystemEntities());

            documentForm.setFileSystemEntities(documentEntity.getFileSystemEntities());

            //update the version number as the value could have changed by rotating receipt image through ajax
            documentForm.setVersion(documentEntity.getVersion());

            bizService.saveNewBusinessAndOrStore(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.saveNewBusinessAndOrStore(documentForm);
            documentForm.setDocumentStatus(TURK_PROCESSED);
            documentForm.setReferenceDocumentId(receipt.getId());
            documentForm.inActive();
            documentManager.save(documentForm);

            updateMessageManager(documentForm, OCR_PROCESSED, TURK_PROCESSED);

            notificationService.addNotification(
                    receipt.getTotalString()
                            + " '" +
                            receipt.getBizName().getBusinessName() +
                            "' " +
                            "receipt processed",
                    NotificationTypeEnum.RECEIPT,
                    receipt);

        } catch (Exception exce) {
            LOG.error("Revert all the transaction for Receipt={}, ReceiptOCR={}, reason={}",
                    receipt.getId(), documentForm.getId(), exce.getLocalizedMessage(), exce);

            //For rollback
            if (StringUtils.isNotEmpty(receipt.getId())) {
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

                documentForm.setDocumentStatus(OCR_PROCESSED);
                documentManager.save(documentForm);
                //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

                messageManager.undoUpdateObject(documentForm.getId(), false, TURK_PROCESSED, OCR_PROCESSED);
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
     * @param receiptDocument
     * @throws Exception
     */
    public void turkProcessReceiptReCheck(ReceiptEntity receipt, List<ItemEntity> items, DocumentEntity receiptDocument) {
        ReceiptEntity fetchedReceipt = null;
        try {
            DocumentEntity documentEntity = loadActiveDocumentById(receiptDocument.getId());

            receipt.setFileSystemEntities(documentEntity.getFileSystemEntities());

            receiptDocument.setFileSystemEntities(documentEntity.getFileSystemEntities());

            //update the version number as the value could have changed by rotating receipt image through ajax
            receiptDocument.setVersion(documentEntity.getVersion());

            bizService.saveNewBusinessAndOrStore(receipt);
            if (StringUtils.isNotEmpty(receipt.getId())) {
                fetchedReceipt = receiptManager.findOne(receipt.getId());
                if (fetchedReceipt == null) {
                    // By creating new receipt with old id, we move the pending receipt from the list back to users account
                    LOG.warn("Something had gone wrong with original Receipt={}, so creating another with old receipt id", receipt.getId());
                } else {
                    receipt.setVersion(fetchedReceipt.getVersion());
                    receipt.setCreated(fetchedReceipt.getCreated());
                }
            }

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.saveNewBusinessAndOrStore(receiptDocument);
            receiptDocument.setDocumentStatus(TURK_PROCESSED);
            receiptDocument.inActive();

            //Only recheck comments are updated by technician. Receipt notes are never modified
            if (StringUtils.isEmpty(receiptDocument.getRecheckComment().getText())) {
                CommentEntity comment = receiptDocument.getRecheckComment();
                commentManager.deleteHard(comment);
                receiptDocument.setRecheckComment(null);
                receipt.setRecheckComment(null);
            } else {
                CommentEntity comment = receiptDocument.getRecheckComment();
                if (StringUtils.isEmpty(comment.getId())) {
                    comment.setId(null);
                }

                /**
                 * If the comment is not equal then it means Technician has modified the comment and this needs
                 * to be updated with new time. Else do not update the time of recheck comment
                 */
                String fetchedRecheckComment = StringUtils.EMPTY;
                if (fetchedReceipt != null && fetchedReceipt.getRecheckComment() != null) {
                    fetchedRecheckComment = fetchedReceipt.getRecheckComment().getText();
                }
                if (!comment.getText().equalsIgnoreCase(fetchedRecheckComment)) {
                    comment.setUpdated();
                    commentManager.save(comment);
                }
                receiptDocument.setRecheckComment(comment);
                receipt.setRecheckComment(comment);
            }

            //Since Technician cannot change notes at least we gotta make sure we are not adding one when the Id for notes are missing
            if (StringUtils.isEmpty(receiptDocument.getNotes().getId())) {
                receiptDocument.setNotes(null);
                receipt.setNotes(null);
            }

            receiptManager.save(receipt);
            documentManager.save(receiptDocument);

            updateMessageManager(receiptDocument, TURK_REQUEST, TURK_PROCESSED);

            notificationService.addNotification(
                    receipt.getTotalString() +
                            " '" +
                            receipt.getBizName().getBusinessName() +
                            "' " +
                            "receipt re-checked",
                    NotificationTypeEnum.RECEIPT,
                    receipt);

        } catch (Exception exce) {
            LOG.error("Revert all the transaction for Receipt={}, ReceiptOCR={}, reason={}",
                    receipt.getId(), receiptDocument.getId(), exce.getLocalizedMessage(), exce);

            //For rollback
            if (StringUtils.isNotEmpty(receipt.getId())) {
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

                receiptDocument.setDocumentStatus(OCR_PROCESSED);
                documentManager.save(receiptDocument);
                //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

                messageManager.undoUpdateObject(receiptDocument.getId(), false, TURK_PROCESSED, TURK_REQUEST);
                //End of roll back

                LOG.warn("Rollback complete for re-processing document");
            }
            throw new RuntimeException("Failed re-processing document " + exce);
        }
    }

    private void updateMessageManager(DocumentEntity receiptOCR, DocumentStatusEnum from, DocumentStatusEnum to) {
        try {
            messageManager.updateObject(receiptOCR.getId(), from, to);
        } catch (Exception exce) {
            LOG.error(exce.getLocalizedMessage());
            messageManager.undoUpdateObject(receiptOCR.getId(), false, to, from);
            throw exce;
        }
    }

    /**
     * Reject receipt when invalid or un-readable.
     *
     * @param documentId
     * @param documentOfType
     * @throws Exception
     */
    public void turkDocumentReject(String documentId, DocumentOfTypeEnum documentOfType) {
        DocumentEntity document = loadActiveDocumentById(documentId);
        try {
            document.setDocumentStatus(TURK_RECEIPT_REJECT);
            document.setDocumentOfType(documentOfType);
            document.setBizName(null);
            document.setBizStore(null);
            document.inActive();
            document.markAsDeleted();
            documentManager.save(document);

            try {
                messageManager.updateObject(document.getId(), OCR_PROCESSED, TURK_RECEIPT_REJECT);
            } catch (Exception exce) {
                LOG.error(exce.getLocalizedMessage());
                messageManager.undoUpdateObject(document.getId(), false, TURK_RECEIPT_REJECT, OCR_PROCESSED);
                throw exce;
            }
            itemOCRManager.deleteWhereReceipt(document);

            fileSystemService.deleteSoft(document.getFileSystemEntities());
            storageManager.deleteSoft(document.getFileSystemEntities());
            GridFSDBFile gridFSDBFile = storageManager.get(document.getFileSystemEntities().iterator().next().getBlobId());
            DBObject dbObject = gridFSDBFile.getMetaData();

            notificationService.addNotification(
                    "Could not process document '" +
                            dbObject.get("ORIGINAL_FILENAME") +
                            "'",
                    NotificationTypeEnum.DOCUMENT,
                    document);

        } catch (Exception exce) {
            LOG.error("Revert all the transaction for ReceiptOCR={}. Rejection of a receipt failed, reason={}",
                    document.getId(), exce.getLocalizedMessage(), exce);

            document.setDocumentStatus(OCR_PROCESSED);
            document.active();
            documentManager.save(document);
            //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

            messageManager.undoUpdateObject(document.getId(), false, TURK_RECEIPT_REJECT, OCR_PROCESSED);
            //End of roll back

            LOG.warn("Rollback complete for rejecting document");
        }
    }

    /**
     * Delete all the associated data with Document like Item OCR, and Message Receipt Entity OCR including
     * deletion of with Document.
     * But cannot delete ReceiptOCR when the receipt has been processed once and now it pending for re-check.
     *
     * @param document
     */
    public void deletePendingDocument(DocumentEntity document) {
        DocumentEntity documentEntity = loadActiveDocumentById(document.getId());
        if (documentEntity == null || !StringUtils.isEmpty(documentEntity.getReferenceDocumentId())) {
            LOG.warn("User trying to delete processed Document={}, Receipt={}",
                    document.getId(), document.getReferenceDocumentId());
        } else {
            deleteReceiptOCR(documentEntity);
        }
    }

    /**
     * Delete all the associated data with Document like Item OCR, and Message Receipt Entity OCR including
     * deletion of with Document.
     * But cannot delete ReceiptOCR when the receipt has been processed once and now it pending for re-check.
     *
     * @param document
     */
    public void deleteRejectedDocument(DocumentEntity document) {
        DocumentEntity documentEntity = loadRejectedDocumentById(document.getId());
        if (documentEntity == null || !StringUtils.isEmpty(documentEntity.getReferenceDocumentId())) {
            LOG.warn("User trying to delete processed Document={}, Receipt={}",
                    document.getId(), document.getReferenceDocumentId());
        } else {
            deleteReceiptOCR(documentEntity);
        }
    }

    private void deleteReceiptOCR(DocumentEntity documentEntity) {
        documentManager.deleteHard(documentEntity);
        itemOCRManager.deleteWhereReceipt(documentEntity);
        messageManager.deleteAllForReceiptOCR(documentEntity.getId());
        storageManager.deleteHard(documentEntity.getFileSystemEntities());
        fileSystemService.deleteHard(documentEntity.getFileSystemEntities());
    }

    /**
     * Populates items with BizNameEntity
     *
     * @param items
     * @param receiptEntity
     */
    private void populateItemsWithBizName(List<ItemEntity> items, ReceiptEntity receiptEntity) {
        for (ItemEntity item : items) {
            item.setBizName(receiptEntity.getBizName());
            populateWithExpenseType(item);
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
    private void populateWithExpenseType(ItemEntity item) {
        if (item.getExpenseTag() != null && item.getExpenseTag().getId() != null) {
            ExpenseTagEntity expenseType = userProfilePreferenceService.getExpenseType(item.getExpenseTag().getId());
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

    public void turkMileage(MileageEntity mileageEntity, DocumentEntity documentForm) {
        try {
            DocumentEntity documentEntity = loadActiveDocumentById(documentForm.getId());

            mileageEntity.setFileSystemEntities(documentEntity.getFileSystemEntities());
            mileageEntity.setDocumentId(documentEntity.getId());
            mileageService.save(mileageEntity);

            documentForm.setFileSystemEntities(documentEntity.getFileSystemEntities());

            //update the version number as the value could have changed by rotating receipt image through ajax
            documentForm.setVersion(documentEntity.getVersion());
            documentForm.setDocumentStatus(TURK_PROCESSED);
            documentForm.setReferenceDocumentId(mileageEntity.getId());
            documentForm.inActive();
            documentManager.save(documentForm);

            updateMessageManager(documentForm, OCR_PROCESSED, TURK_PROCESSED);

            notificationService.addNotification(
                    String.valueOf(mileageEntity.getStart()) +
                            ", " +
                            "odometer reading processed",
                    NotificationTypeEnum.MILEAGE,
                    mileageEntity);
        } catch (DuplicateKeyException duplicateKeyException) {
            LOG.error(duplicateKeyException.getLocalizedMessage(), duplicateKeyException);
            throw new RuntimeException("Found existing record with similar odometer reading");
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            //add roll back
        }
    }
}
