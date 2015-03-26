package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.joda.time.DateTime;

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
    private boolean notified = false;

    @JsonProperty ("nt")
    private String notificationType;

    /**
     * Could be a receipt id or Document id
     */
    @JsonProperty ("ri")
    private String referenceId;

    @JsonProperty ("c")
    private String created;

    @JsonProperty ("u")
    private String updated;

    private JsonNotification(NotificationEntity notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.notified = notification.isNotified();
        this.notificationType = notification.getNotificationType().getName();
        this.referenceId = notification.getReferenceId();
        this.created = JsonReceipt.FMT.print(new DateTime(notification.getCreated()));
        this.updated = JsonReceipt.FMT.print(new DateTime(notification.getUpdated()));
    }

    public static JsonNotification newInstance(NotificationEntity notification) {
        return new JsonNotification(notification);
    }
}
