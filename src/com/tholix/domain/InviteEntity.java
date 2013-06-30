package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:06 PM
 */
@Document(collection = "INVITE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "invite_key_idx", def = "{'authenticationKey' : 0}", unique = true)
} )
public class InviteEntity extends BaseEntity {

    @NotNull
    private String emailId;

    @NotNull
    private String authenticationKey;

    @DBRef
    @Field("invited")
    private UserProfileEntity newInvitedUser;

    @DBRef
    @Field("invited_by")
    private UserProfileEntity userProfile;

    private InviteEntity(String emailId, String authenticationKey, UserProfileEntity newInvitedUser, UserProfileEntity userProfile) {
        this.emailId = emailId;
        this.authenticationKey = authenticationKey;
        this.newInvitedUser = newInvitedUser;
        this.userProfile = userProfile;
    }

    public static InviteEntity newInstance(String emailId, String authenticationKey, UserProfileEntity newInvitedUser, UserProfileEntity userProfile) {
        return new InviteEntity(emailId, authenticationKey, newInvitedUser, userProfile);
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public UserProfileEntity getNewInvitedUser() {
        return newInvitedUser;
    }

    public void setNewInvitedUser(UserProfileEntity newInvitedUser) {
        this.newInvitedUser = newInvitedUser;
    }

    public UserProfileEntity getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileEntity userProfile) {
        this.userProfile = userProfile;
    }
}
