package com.receiptofi.domain.json;

import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.ProviderEnum;

import java.util.Date;

/**
 * User: hitender
 * Date: 9/17/15 3:56 PM
 */
public class JsonAwaitingAcceptance {

    private String id;
    private String authKey;
    private Date created;
    private String initials;
    private String name;
    private String email;
    private boolean active;
    private ProviderEnum provider;

    public JsonAwaitingAcceptance(FriendEntity friend, UserProfileEntity userProfile) {
        this.created = friend.getCreated();
        this.id = friend.getId();
        this.authKey = friend.getAuthenticationKey();

        this.initials = userProfile.getInitials();
        this.name = userProfile.getName();
        this.email = userProfile.getEmail();
        this.active = userProfile.isActive();
        this.provider = userProfile.getProviderId();
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

    public boolean isActive() {
        return active;
    }

    public ProviderEnum getProvider() {
        return provider;
    }
}
