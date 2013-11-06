package com.receiptofi.domain;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.receiptofi.domain.types.ReceiptStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 6:48 PM
 */
@Document(collection = "MESSAGE_RECEIPT_OCR")
public class MessageReceiptEntityOCR extends BaseEntity {

    @NotNull
    @Field("RECEIPT_OCR_ID")
    private String receiptOCRId;

    @NotNull
    @Field("USER_LEVEL_ENUM")
    private UserLevelEnum level = UserLevelEnum.USER;

    @Email
    @Field("EMAIL")
    String emailId;

    @NotNull
    @Field("USER_PROFILE_ID")
    String userProfileId;

    @NotNull
    @Field("LOCKED")
    private boolean recordLocked = false;

    @NotNull
    @Field("RECEIPT_STATUS_ENUM")
    private ReceiptStatusEnum receiptStatus;

    private MessageReceiptEntityOCR() {}

    private MessageReceiptEntityOCR(String receiptOCRId, UserLevelEnum level, ReceiptStatusEnum receiptStatus) {
        this.receiptOCRId = receiptOCRId;
        this.level = level;
        this.receiptStatus = receiptStatus;
    }

    public static MessageReceiptEntityOCR newInstance(String idReceiptOCR, UserLevelEnum level, ReceiptStatusEnum receiptStatus) {
        return new MessageReceiptEntityOCR(idReceiptOCR, level, receiptStatus);
    }

    public String getReceiptOCRId() {
        return receiptOCRId;
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

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public boolean isRecordLocked() {
        return recordLocked;
    }

    public void setRecordLocked(boolean recordLocked) {
        this.recordLocked = recordLocked;
    }

    public ReceiptStatusEnum getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(ReceiptStatusEnum receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    @Override
    public String toString() {
        return "MessageReceiptEntityOCR{" +
                "id='" + id + '\'' +
                ", receiptOCRId='" + receiptOCRId + '\'' +
                ", level=" + level +
                ", emailId='" + emailId + '\'' +
                ", userProfileId='" + userProfileId + '\'' +
                ", recordLocked=" + recordLocked +
                ", receiptStatus=" + receiptStatus +
                '}';
    }
}
