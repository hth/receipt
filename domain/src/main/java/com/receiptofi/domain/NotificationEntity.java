package com.receiptofi.domain;

import com.receiptofi.domain.types.NotificationTypeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "NOTIFICATION")
/** Updated index. */
@CompoundIndexes (value = {
        @CompoundIndex (name = "notification_idx", def = "{'C': -1, 'RID': 1}", background = true)
})
public class NotificationEntity extends BaseEntity {

    @NotNull
    @Field ("MS")
    private String message;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    /** Notify this notification when true otherwise do not notify using messaging. */
    @NotNull
    @Field ("ND")
    private boolean notified = false;

    @NotNull
    @Field ("NNE")
    private NotificationTypeEnum notificationType;

    /**
     * Could be a receipt id or Document id or empty when its just an invite.
     */
    @Field ("REF")
    private String referenceId;

    @NotNull
    @Field ("CN")
    private int count = 0;

    @SuppressWarnings ("unused")
    private NotificationEntity() {
        super();
    }

    private NotificationEntity(NotificationTypeEnum notificationType) {
        super();
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

    private void setNotified(boolean notified) {
        this.notified = notified;
    }

    public void markThisToSendNotification() {
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

    public int getCount() {
        return count;
    }

    public void addCount() {
        this.count += 1;
    }
}
