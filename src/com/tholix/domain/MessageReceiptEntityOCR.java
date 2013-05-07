package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import org.hibernate.validator.constraints.Email;

import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.utils.DateUtil;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 6:48 PM
 */
@Document(collection = "MESSAGE_RECEIPT_OCR")
public class MessageReceiptEntityOCR extends BaseEntity {


    @NotNull
    private String idReceiptOCR;

    @Size(min = 0, max = 128)
    private String description;

    @NotNull
    private UserLevelEnum level = UserLevelEnum.USER;

    @Email
    String emailId;

    String userProfileId;

    @NotNull
    private boolean recordLocked = false;

    @NotNull
    private ReceiptStatusEnum receiptStatus;

    @Transient
    private String since;

    private MessageReceiptEntityOCR() {}

    private MessageReceiptEntityOCR(String idReceiptOCR, String description, UserLevelEnum level, ReceiptStatusEnum receiptStatus) {
        this.idReceiptOCR = idReceiptOCR;
        this.description = description;
        this.level = level;
        this.receiptStatus = receiptStatus;
    }

    public static MessageReceiptEntityOCR newInstance(String idReceiptOCR, String description, UserLevelEnum level, ReceiptStatusEnum receiptStatus) {
        return new MessageReceiptEntityOCR(idReceiptOCR, description, level, receiptStatus);
    }

    public String getIdReceiptOCR() {
        return idReceiptOCR;
    }

    public String getDescription() {
        return description;
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

    public String getSince() {
        return DateUtil.getDurationStr(getCreated());
    }

    @Override
    public String toString() {
        return "MessageReceiptEntityOCR{" +
                "id='" + id + '\'' +
                ", idReceiptOCR='" + idReceiptOCR + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                ", emailId='" + emailId + '\'' +
                ", userProfileId='" + userProfileId + '\'' +
                ", recordLocked=" + recordLocked +
                ", receiptStatus=" + receiptStatus +
                '}';
    }
}
