package com.receiptofi.service;

import static com.receiptofi.domain.types.DocumentStatusEnum.PENDING;
import static com.receiptofi.domain.types.DocumentStatusEnum.PROCESSED;
import static com.receiptofi.domain.types.DocumentStatusEnum.REJECT;
import static com.receiptofi.domain.types.DocumentStatusEnum.REPROCESS;

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
import com.receiptofi.repository.MessageDocumentManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Autowired private DocumentManager documentManager;
    @Autowired private ItemOCRManager itemOCRManager;
    @Autowired private ReceiptManager receiptManager;
    @Autowired private ItemManager itemManager;
    @Autowired private MessageDocumentManager messageDocumentManager;
    @Autowired private BizService bizService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private CommentManager commentManager;
    @Autowired private NotificationService notificationService;
    @Autowired private StorageManager storageManager;
    @Autowired private FileSystemService fileSystemService;
    @Autowired private MileageService mileageService;

    public DocumentEntity loadActiveDocumentById(String documentId) {
        return documentManager.findActiveOne(documentId);
    }

    public DocumentEntity loadRejectedDocumentById(String documentId) {
        return documentManager.findRejectedOne(documentId);
    }

    public DocumentEntity findOne(String documentId, String userProfileId) {
        return documentManager.findOne(documentId, userProfileId);
    }

    public List<ItemEntityOCR> loadItemsOfReceipt(DocumentEntity receipt) {
        return itemOCRManager.getWhereReceipt(receipt);
    }

    public List<DocumentEntity> getAllProcessedDocuments() {
        return documentManager.getAllProcessedDocuments();
    }

    public void cloudUploadSuccessful(String documentId) {
        documentManager.cloudUploadSuccessful(documentId);
    }

    /**
     * This method is used when technician saves the processed receipt for the first time.
     *
     * @param receipt
     * @param items
     * @param document
     * @throws Exception
     */
    public void processDocumentForReceipt(String technicianId, ReceiptEntity receipt, List<ItemEntity> items, DocumentEntity document) {
        try {
            DocumentEntity documentEntity = loadActiveDocumentById(document.getId());

            receipt.setFileSystemEntities(documentEntity.getFileSystemEntities());

            document.setFileSystemEntities(documentEntity.getFileSystemEntities());

            //update the version number as the value could have changed by rotating receipt image through ajax
            document.setVersion(documentEntity.getVersion());

            bizService.saveNewBusinessAndOrStore(receipt);
            receiptManager.save(receipt);

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.copyBizNameAndBizStoreFromReceipt(document, receipt);
            document.setDocumentStatus(PROCESSED);
            document.setReferenceDocumentId(receipt.getId());
            document.inActive();
            document.addProcessedBy(new Date(), technicianId);
            documentManager.save(document);

            updateMessageManager(document, PENDING, PROCESSED);

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
                    receipt.getId(), document.getId(), exce.getLocalizedMessage(), exce);

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

                document.setDocumentStatus(PENDING);
                documentManager.save(document);
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
    public void processDocumentReceiptReCheck(String technicianId, ReceiptEntity receipt, List<ItemEntity> items, DocumentEntity document) {
        ReceiptEntity fetchedReceipt = null;
        try {
            DocumentEntity documentEntity = loadActiveDocumentById(document.getId());

            receipt.setFileSystemEntities(documentEntity.getFileSystemEntities());

            document.setFileSystemEntities(documentEntity.getFileSystemEntities());

            //update the version number as the value could have changed by rotating receipt image through ajax
            document.setVersion(documentEntity.getVersion());

            bizService.saveNewBusinessAndOrStore(receipt);
            if (StringUtils.isNotEmpty(receipt.getId())) {
                fetchedReceipt = receiptManager.findOne(receipt.getId());
                if (null == fetchedReceipt) {
                    // By creating new receipt with old id, we move the pending receipt from the list back to users account
                    LOG.warn("Something had gone wrong with original Receipt={}, " +
                            "so creating another with old receipt id", receipt.getId());
                } else {
                    receipt.setVersion(fetchedReceipt.getVersion());
                    receipt.setCreated(fetchedReceipt.getCreated());
                }
            }

            populateItemsWithBizName(items, receipt);
            itemManager.saveObjects(items);

            bizService.copyBizNameAndBizStoreFromReceipt(document, receipt);
            document.setDocumentStatus(PROCESSED);
            document.inActive();
            document.addProcessedBy(new Date(), technicianId);

            //Only recheck comments are updated by technician. Receipt notes are never modified
            if (StringUtils.isEmpty(document.getRecheckComment().getText())) {
                CommentEntity comment = document.getRecheckComment();
                commentManager.deleteHard(comment);
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
                String fetchedRecheckComment = StringUtils.EMPTY;
                if (null != fetchedReceipt && null != fetchedReceipt.getRecheckComment()) {
                    fetchedRecheckComment = fetchedReceipt.getRecheckComment().getText();
                }
                if (!comment.getText().equalsIgnoreCase(fetchedRecheckComment)) {
                    comment.setUpdated();
                    commentManager.save(comment);
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

            receiptManager.save(receipt);
            documentManager.save(document);

            updateMessageManager(document, REPROCESS, PROCESSED);

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
                    receipt.getId(), document.getId(), exce.getLocalizedMessage(), exce);

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

                document.setDocumentStatus(PENDING);
                documentManager.save(document);
                //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

                messageDocumentManager.undoUpdateObject(document.getId(), false, PROCESSED, REPROCESS);
                //End of roll back

                LOG.warn("Rollback complete for re-processing document");
            }
            throw new RuntimeException("Failed re-processing document " + exce);
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
     * @param documentId
     * @param documentOfType
     * @throws Exception
     */
    public void processDocumentForReject(String technicianId, String documentId, DocumentOfTypeEnum documentOfType) {
        DocumentEntity document = loadActiveDocumentById(documentId);
        try {
            document.setDocumentStatus(REJECT);
            document.setDocumentOfType(documentOfType);
            document.setBizName(null);
            document.setBizStore(null);
            document.inActive();
            document.addProcessedBy(new Date(), technicianId);
            document.markAsDeleted();
            documentManager.save(document);

            updateMessageWithDocumentChanges(document);
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

            document.setDocumentStatus(PENDING);
            document.active();
            documentManager.save(document);
            //LOG.error("Failed to rollback Document: " + documentForm.getId() + ", error message: " + e.getLocalizedMessage());

            messageDocumentManager.undoUpdateObject(document.getId(), false, REJECT, PENDING);
            //End of roll back

            LOG.warn("Rollback complete for rejecting document");
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
        DocumentEntity documentEntity = loadActiveDocumentById(document.getId());
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
        DocumentEntity documentEntity = loadRejectedDocumentById(document.getId());
        if (null == documentEntity || !StringUtils.isEmpty(documentEntity.getReferenceDocumentId())) {
            LOG.warn("User trying to delete processed Document={}, Receipt={}",
                    document.getId(), document.getReferenceDocumentId());
        } else {
            deleteReceiptOCR(documentEntity);
        }
    }

    private void deleteReceiptOCR(DocumentEntity document) {
        documentManager.deleteHard(document);
        itemOCRManager.deleteWhereReceipt(document);
        messageDocumentManager.deleteAllForReceiptOCR(document.getId());
        storageManager.deleteHard(document.getFileSystemEntities());
        fileSystemService.deleteHard(document.getFileSystemEntities());
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
        if (null != item.getExpenseTag() && null != item.getExpenseTag().getId()) {
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

    /**
     * Processes Mileage document.
     * @param technicianId
     * @param mileageEntity
     * @param document
     */
    public void processDocumentForMileage(String technicianId, MileageEntity mileageEntity, DocumentEntity document) {
        try {
            DocumentEntity documentEntity = loadActiveDocumentById(document.getId());

            mileageEntity.setFileSystemEntities(documentEntity.getFileSystemEntities());
            mileageEntity.setDocumentId(documentEntity.getId());
            mileageService.save(mileageEntity);

            document.setFileSystemEntities(documentEntity.getFileSystemEntities());

            //update the version number as the value could have changed by rotating receipt image through ajax
            document.setVersion(documentEntity.getVersion());
            document.setDocumentStatus(PROCESSED);
            document.setReferenceDocumentId(mileageEntity.getId());
            document.inActive();
            document.addProcessedBy(new Date(), technicianId);
            documentManager.save(document);

            updateMessageManager(document, PENDING, PROCESSED);

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
