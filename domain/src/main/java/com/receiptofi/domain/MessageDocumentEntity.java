package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

import org.hibernate.validator.constraints.Email;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 6:48 PM
 */
@Document (collection = "MESSAGE_DOCUMENT")
public final class MessageDocumentEntity extends BaseEntity {

    @Email
    @Field ("EM")
    String emailId;

    @NotNull
    @Field ("RID")
    String receiptUserId;

    //TODO change to document id
    @NotNull
    @Field ("DID")
    private String documentId;

    @NotNull
    @Field ("ULE")
    private UserLevelEnum level = UserLevelEnum.USER;

    @NotNull
    @Field ("LOK")
    private boolean recordLocked = false;

    @NotNull
    @Field ("DS")
    private DocumentStatusEnum documentStatus;

    @SuppressWarnings ("unused")
    private MessageDocumentEntity() {
    }

    private MessageDocumentEntity(String documentId, UserLevelEnum level, DocumentStatusEnum documentStatus) {
        this.documentId = documentId;
        this.level = level;
        this.documentStatus = documentStatus;
    }

    public static MessageDocumentEntity newInstance(String idReceiptOCR, UserLevelEnum level, DocumentStatusEnum receiptStatus) {
        return new MessageDocumentEntity(idReceiptOCR, level, receiptStatus);
    }

    public String getDocumentId() {
        return documentId;
    }

    public UserLevelEnum getLevel() {
        return level;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public boolean isRecordLocked() {
        return recordLocked;
    }

    public void setRecordLocked(boolean recordLocked) {
        this.recordLocked = recordLocked;
    }

    public DocumentStatusEnum getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatusEnum documentStatus) {
        this.documentStatus = documentStatus;
    }

    @Override
    public String toString() {
        return "MessageDocumentEntity{" +
                "id='" + id + '\'' +
                ", documentId='" + documentId + '\'' +
                ", level=" + level +
                ", emailId='" + emailId + '\'' +
                ", receiptUserId='" + receiptUserId + '\'' +
                ", recordLocked=" + recordLocked +
                ", documentStatus=" + documentStatus +
                '}';
    }
}
