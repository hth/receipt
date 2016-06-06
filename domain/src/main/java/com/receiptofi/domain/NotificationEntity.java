package com.receiptofi.domain;

import com.receiptofi.domain.types.NotificationGroupEnum;
import com.receiptofi.domain.types.NotificationMarkerEnum;
import com.receiptofi.domain.types.NotificationStateEnum;
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

    /**
     * Various kinds of notifications.
     * Mark the notification Simple Notify, Push Notify, Ignore Notifying.
     */
    @NotNull
    @Field ("NM")
    private NotificationMarkerEnum notificationMarkerEnum;

    /**
     * Set the type of notification for Push Notification, Document.
     */
    @NotNull
    @Field ("NNE")
    private NotificationTypeEnum notificationType;

    /**
     * Grouped to match similar messages with respective icons.
     * Could be ground in Social, File, Receipt, Normal Message.
     */
    @NotNull
    @Field ("NG")
    private NotificationGroupEnum notificationGroup;

    /**
     * Could be a receipt id or Document id or empty when its just an invite.
     */
    @Field ("REF")
    private String referenceId;

    @NotNull
    @Field ("CN")
    private int count = 0;

    /** Success or Failure in notifying. */
    @NotNull
    @Field ("NS")
    private NotificationStateEnum notificationStateEnum = NotificationStateEnum.F;

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

    public NotificationMarkerEnum getNotificationMarkerEnum() {
        return notificationMarkerEnum;
    }

    public void setNotificationMarkerEnum(NotificationMarkerEnum notificationMarkerEnum) {
        this.notificationMarkerEnum = notificationMarkerEnum;
    }

    public boolean isNotify() {
        return notificationMarkerEnum != NotificationMarkerEnum.I;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    private void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationGroupEnum getNotificationGroup() {
        return notificationGroup;
    }

    public void setNotificationGroup(NotificationGroupEnum notificationGroup) {
        this.notificationGroup = notificationGroup;
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

    public NotificationStateEnum getNotificationStateEnum() {
        return notificationStateEnum;
    }

    public void setNotificationStateToSuccess() {
        this.notificationStateEnum = NotificationStateEnum.S;
    }

    public void setNotificationStateToFailure() {
        this.notificationStateEnum = NotificationStateEnum.F;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "message='" + message + '\'' +
                ", receiptUserId='" + receiptUserId + '\'' +
                ", notificationMarkerEnum=" + notificationMarkerEnum +
                ", notificationType=" + notificationType +
                ", notificationGroup=" + notificationGroup +
                ", referenceId='" + referenceId + '\'' +
                ", count=" + count +
                ", notificationStateEnum=" + notificationStateEnum +
                '}';
    }
}
