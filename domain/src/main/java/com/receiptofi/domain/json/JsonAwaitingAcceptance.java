package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.ProviderEnum;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 9/17/15 3:56 PM
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
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Mobile
public class JsonAwaitingAcceptance {
    private static final Logger LOG = LoggerFactory.getLogger(JsonAwaitingAcceptance.class);

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("au")
    private String authKey;

    @JsonProperty ("c")
    private String created;

    @JsonProperty ("initials")
    private String initials;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("em")
    private String email;

    @JsonProperty ("pr")
    private String provider = "";

    @JsonProperty ("a")
    private boolean active;

    public JsonAwaitingAcceptance(FriendEntity friend, UserProfileEntity userProfile) {
        if (null == userProfile) {
            LOG.error("UserProfile cannot be null rid={} fid={}", friend.getReceiptUserId(), friend.getFriendUserId());
        } else {
            this.initials = userProfile.getInitials();
            this.name = userProfile.getName();
            this.email = userProfile.getEmail();
            this.provider = userProfile.getProviderId() == null ? "" : userProfile.getProviderId().name();
            this.active = userProfile.isActive();
        }

        if (null == friend) {
            LOG.error("Friend cannot be null");
        } else {
            this.created = DateFormatUtils.format(friend.getCreated(), JsonReceipt.ISO8601_FMT, TimeZone.getTimeZone("UTC"));
            this.id = friend.getId();
            this.authKey = friend.getAuthenticationKey();
        }
    }

    public String getId() {
        return id;
    }

    public String getAuthKey() {
        return authKey;
    }

    public Date getCreated() {
        return created;
    }

    public String getInitials() {
        return initials;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isActive() {
        return active;
    }
}
