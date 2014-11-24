package com.receiptofi.domain;

import com.receiptofi.domain.types.NotificationTypeEnum;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:29 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Document (collection = "NOTIFICATION")
public final class NotificationEntity extends BaseEntity {

    @NotNull
    @Field ("MS")
    private String message;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("ND")
    private boolean notified = false;

    @NotNull
    @Field ("NNE")
    private NotificationTypeEnum notificationType;

    /**
     * Could be a receipt id or Document id
     */
    @NotNull
    @Field ("REF")
    private String referenceId;

    @SuppressWarnings ("unused")
    private NotificationEntity() {
    }

    private NotificationEntity(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public static NotificationEntity newInstance(NotificationTypeEnum notificationType) {
        return new NotificationEntity(notificationType);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public void markAsNotified() {
        setNotified(true);
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    private void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
