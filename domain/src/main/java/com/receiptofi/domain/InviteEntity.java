package com.receiptofi.domain;

import com.receiptofi.domain.types.UserLevelEnum;

import org.hibernate.validator.constraints.Email;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:06 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "INVITE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "invite_email_idx", def = "{'EM': 0}", unique = false),
        @CompoundIndex (name = "invite_key_idx", def = "{'AU' : 0}", unique = true)
})
public class InviteEntity extends BaseEntity {

    @NotNull
    @Field ("EM")
    @Email
    private String email;

    @NotNull
    @Field ("AU")
    private String authenticationKey;

    @DBRef
    @Field ("IN")
    private UserProfileEntity invited;

    @DBRef
    @Field ("INV")
    private UserAccountEntity invitedBy;

    /* Set the kind of invite like Business or Enterprise or for User. */
    @NotNull
    @Field ("UL")
    private UserLevelEnum userLevel;

    private InviteEntity(
            String email,
            String authenticationKey,
            UserProfileEntity invited,
            UserAccountEntity invitedBy,
            UserLevelEnum userLevel
    ) {
        super();
        this.email = email;
        this.authenticationKey = authenticationKey;
        this.invited = invited;
        this.invitedBy = invitedBy;
        this.userLevel = userLevel;
    }

    public static InviteEntity newInstance(
            String email,
            String authenticationKey,
            UserProfileEntity invited,
            UserAccountEntity invitedBy,
            UserLevelEnum userLevel
    ) {
        return new InviteEntity(email, authenticationKey, invited, invitedBy, userLevel);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public UserProfileEntity getInvited() {
        return invited;
    }

    public void setInvited(UserProfileEntity invited) {
        this.invited = invited;
    }

    public UserAccountEntity getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(UserAccountEntity invitedBy) {
        this.invitedBy = invitedBy;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }
}
