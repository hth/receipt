/**
 *
 */
package com.receiptofi.domain;

import com.receiptofi.domain.types.CardNetworkEnum;
import com.receiptofi.domain.types.DocumentOfTypeEnum;
import com.receiptofi.domain.types.DocumentRejectReasonEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * @author hitender
 * @since Jan 6, 2013 1:04:43 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "DOCUMENT")
@CompoundIndexes ({@CompoundIndex (name = "document_idx", def = "{'FS': 1, 'RID': 1}")})
public class DocumentEntity extends BaseEntity {

    @NotNull
    @Field ("DS")
    private DocumentStatusEnum documentStatus;

    @DBRef
    @Field ("FS")
    private Collection<FileSystemEntity> fileSystemEntities;

    @NotNull
    @Field ("RTXD")
    private String receiptDate;

    @Transient
    @Field ("STOT")
    private String subTotal;

    @NotNull
    @Field ("TOT")
    private String total;

    @NotNull
    @Field ("TAX")
    private String tax = "0.00";

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field ("BIZ_STORE")
    private BizStoreEntity bizStore;

    @Field ("RDID")
    private String referenceDocumentId;

    @DBRef
    @Field ("CR")
    private CommentEntity recheckComment;

    @DBRef
    @Field ("NO")
    private CommentEntity notes;

    @NotNull
    @Field ("DT")
    private DocumentOfTypeEnum documentOfType;

    /** Defaults to DocumentRejectReasonEnum.G because validation of null enum is such a pain. */
    @Field ("RR")
    private DocumentRejectReasonEnum documentRejectReason = DocumentRejectReasonEnum.G;

    @SuppressWarnings ("unused")
    @Field ("IU")
    private boolean imageUploadedToCloud;

    @Field ("PB")
    private Map<Date, String> processedBy = new LinkedHashMap<>();

    @Field ("NU")
    private boolean notifyUser;

    @Field ("CN")
    private CardNetworkEnum cardNetwork;

    @Field ("CD")
    private String cardDigit;

    public static DocumentEntity newInstance() {
        return new DocumentEntity();
    }

    public DocumentStatusEnum getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatusEnum documentStatus) {
        this.documentStatus = documentStatus;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public void setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
    }

    public void addReceiptBlobId(FileSystemEntity receiptBlobId) {
        if (null == this.fileSystemEntities) {
            this.fileSystemEntities = new ArrayList<>();
        }
        this.fileSystemEntities.add(receiptBlobId);
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String userProfileId) {
        this.receiptUserId = userProfileId;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public void setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
    }

    public String getReferenceDocumentId() {
        return referenceDocumentId;
    }

    public void setReferenceDocumentId(String referenceDocumentId) {
        this.referenceDocumentId = referenceDocumentId;
    }

    public CommentEntity getRecheckComment() {
        return recheckComment;
    }

    public void setRecheckComment(CommentEntity recheckComment) {
        this.recheckComment = recheckComment;
    }

    public CommentEntity getNotes() {
        return notes;
    }

    public void setNotes(CommentEntity notes) {
        this.notes = notes;
    }

    @SuppressWarnings ("unused")
    public DocumentOfTypeEnum getDocumentOfType() {
        return documentOfType;
    }

    public void setDocumentOfType(DocumentOfTypeEnum documentOfType) {
        this.documentOfType = documentOfType;
    }

    public DocumentRejectReasonEnum getDocumentRejectReason() {
        return documentRejectReason;
    }

    public void setDocumentRejectReason(DocumentRejectReasonEnum documentRejectReason) {
        this.documentRejectReason = documentRejectReason;
    }

    @SuppressWarnings ("unused")
    public boolean isImageUploadedToCloud() {
        return imageUploadedToCloud;
    }

    public Map<Date, String> getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Map<Date, String> processedBy) {
        this.processedBy = processedBy;
    }

    public void addProcessedBy(Date updated, String rid) {
        this.processedBy.put(updated, rid);
    }

    public boolean isNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(boolean notifyUser) {
        this.notifyUser = notifyUser;
    }

    public CardNetworkEnum getCardNetwork() {
        return cardNetwork;
    }

    public void setCardNetwork(CardNetworkEnum cardNetwork) {
        this.cardNetwork = cardNetwork;
    }

    public String getCardDigit() {
        return cardDigit;
    }

    public void setCardDigit(String cardDigit) {
        this.cardDigit = cardDigit.trim();
    }

    @Override
    public String toString() {
        return "DocumentEntity{" +
                "documentStatus=" + documentStatus +
                ", fileSystemEntities=" + fileSystemEntities +
                ", receiptDate='" + receiptDate + '\'' +
                ", subTotal='" + subTotal + '\'' +
                ", total='" + total + '\'' +
                ", tax='" + tax + '\'' +
                ", receiptUserId='" + receiptUserId + '\'' +
                ", bizName=" + bizName +
                ", bizStore=" + bizStore +
                ", referenceDocumentId='" + referenceDocumentId + '\'' +
                ", recheckComment=" + recheckComment +
                ", notes=" + notes +
                ", documentOfType=" + documentOfType +
                ", documentRejectReason=" + documentRejectReason +
                ", imageUploadedToCloud=" + imageUploadedToCloud +
                ", processedBy=" + processedBy +
                ", notifyUser=" + notifyUser +
                ", cardNetwork=" + cardNetwork +
                ", cardDigit='" + cardDigit + '\'' +
                '}';
    }
}
