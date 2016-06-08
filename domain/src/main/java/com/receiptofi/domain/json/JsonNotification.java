package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.TimeZone;

/**
 * User: hitender
 * Date: 3/25/15 10:23 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
@Mobile
public class JsonNotification {

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("m")
    private String message;

    @JsonProperty ("n")
    private boolean notify;

    @JsonProperty ("nt")
    private String notificationType;

    /** @since 1.0.3 */
    @JsonProperty ("ng")
    private String notificationGroup;

    /**
     * Could be a receipt id or Document id
     */
    @JsonProperty ("ri")
    private String referenceId;

    @JsonProperty("mr")
    private boolean markedRead;

    @JsonProperty ("c")
    private String created;

    @JsonProperty ("u")
    private String updated;

    @JsonProperty ("a")
    private boolean active;

    private JsonNotification(NotificationEntity notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.notify = notification.isNotify();
        this.notificationType = notification.getNotificationType().getName();
        this.notificationGroup = notification.getNotificationGroup().getName();
        this.referenceId = notification.getReferenceId() == null ? "" : notification.getReferenceId();
        this.markedRead = notification.isMarkedRead();
        this.created = DateFormatUtils.format(notification.getCreated(), JsonReceipt.ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.updated = DateFormatUtils.format(notification.getUpdated(), JsonReceipt.ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.active = notification.isActive();
    }

    public static JsonNotification newInstance(NotificationEntity notification) {
        return new JsonNotification(notification);
    }
}
