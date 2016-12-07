/**
 *
 */
package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * @author hitender
 * @since Dec 23, 2012 1:48:36 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_PREFERENCE")
@CompoundIndexes ({
        @CompoundIndex (name = "user_preference_idx", def = "{'RID': 1}", unique = true, background = true)
})
public class UserPreferenceEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @DBRef
    @Indexed (unique = true)
    @Field ("USER_PROFILE")
    private UserProfileEntity userProfile;

    /**
     * To make bean happy
     */
    @SuppressWarnings ("unused")
    private UserPreferenceEntity() {
        super();
    }

    // @PersistenceConstructor
    private UserPreferenceEntity(UserProfileEntity userProfile) {
        super();
        this.userProfile = userProfile;
        this.receiptUserId = userProfile.getReceiptUserId();
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    /**
     * This method is used when the Entity is created for the first time.
     *
     * @param userProfile
     * @return
     */
    public static UserPreferenceEntity newInstance(UserProfileEntity userProfile) {
        return new UserPreferenceEntity(userProfile);
    }

    public UserProfileEntity getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileEntity userProfile) {
        this.userProfile = userProfile;
    }
}
