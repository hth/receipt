package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 12:02 AM
 */
@Document(collection = "FORGOT_RECOVER")
@CompoundIndexes(value = {
        @CompoundIndex(name = "forgot_recover_idx",     def = "{'userProfileId': 0, 'created': 1}"),
        @CompoundIndex(name = "forgot_recover_key_idx", def = "{'authenticationKey' : 0}", unique = true)
} )
public class ForgotRecoverEntity extends BaseEntity {

    @NotNull
    private String userProfileId;

    @NotNull
    private String authenticationKey;

    private ForgotRecoverEntity(String userProfileId, String authenticationKey) {
        this.userProfileId = userProfileId;
        this.authenticationKey = authenticationKey;
    }

    public static ForgotRecoverEntity newInstance(String userProfileId, String authenticationKey) {
        return new ForgotRecoverEntity(userProfileId, authenticationKey);
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}
