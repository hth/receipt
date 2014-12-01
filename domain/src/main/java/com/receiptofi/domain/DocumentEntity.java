/**
 *
 */
package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentOfTypeEnum;
import com.receiptofi.domain.types.DocumentStatusEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collection;

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
public final class DocumentEntity extends BaseEntity {

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
    @Field ("CN")
    private CommentEntity notes;

    @NotNull
    @Field ("DT")
    private DocumentOfTypeEnum documentOfType;

    @Field ("IU")
    private boolean imageUploadedToCloud;

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

    public boolean isImageUploadedToCloud() {
        return imageUploadedToCloud;
    }

    public void setImageUploadedToCloud(boolean imageUploadedToCloud) {
        this.imageUploadedToCloud = imageUploadedToCloud;
    }

    @Override
    public String toString() {
        return "DocumentEntity{" +
                "id=" + id +
                ", documentStatus=" + documentStatus +
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
                '}';
    }
}
