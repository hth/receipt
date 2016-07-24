package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 6:48 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "MESSAGE_DOCUMENT")
@CompoundIndexes ({@CompoundIndex (name = "message_document_idx", def = "{'RID': -1, 'DID': 1}")})
public class MessageDocumentEntity extends BaseEntity {

    @Field ("EM")
    private String emailId;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("DID")
    private String documentId;

    @NotNull
    @Field ("UL")
    private UserLevelEnum level = UserLevelEnum.USER;

    @NotNull
    @Field ("LOK")
    private boolean recordLocked = false;

    @NotNull
    @Field ("DS")
    private DocumentStatusEnum documentStatus;

    @SuppressWarnings ("unused")
    private MessageDocumentEntity() {
        super();
    }

    private MessageDocumentEntity(
            String documentId,
            UserLevelEnum level,
            DocumentStatusEnum documentStatus
    ) {
        super();
        this.documentId = documentId;
        this.level = level;
        this.documentStatus = documentStatus;
    }

    public static MessageDocumentEntity newInstance(
            String documentId,
            UserLevelEnum level,
            DocumentStatusEnum receiptStatus
    ) {
        return new MessageDocumentEntity(documentId, level, receiptStatus);
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
