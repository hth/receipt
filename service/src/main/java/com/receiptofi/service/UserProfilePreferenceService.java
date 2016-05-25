package com.receiptofi.service;

import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 9:37 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class UserProfilePreferenceService {

    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;

    @Autowired
    public UserProfilePreferenceService(
            UserProfileManager userProfileManager,
            UserPreferenceManager userPreferenceManager) {

        this.userProfileManager = userProfileManager;
        this.userPreferenceManager = userPreferenceManager;
    }

    public UserProfileEntity findByEmail(String email) {
        return userProfileManager.findByEmail(email);
    }

    public UserProfileEntity findByReceiptUserId(String rid) {
        return userProfileManager.findByReceiptUserId(rid);
    }

    public UserProfileEntity forProfilePreferenceFindByReceiptUserId(String rid) {
        return userProfileManager.forProfilePreferenceFindByReceiptUserId(rid);
    }

    public UserProfileEntity findByProviderUserId(String puid) {
        return userProfileManager.findByProviderUserId(puid);
    }

    public void updateProfile(UserProfileEntity userProfile) {
        userProfileManager.save(userProfile);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    @Mobile
    @SuppressWarnings ("unused")
    public UserProfileEntity getProfileUpdateSince(String rid, Date since) {
        return userProfileManager.getProfileUpdateSince(rid, since);
    }

    public void deleteHard(UserProfileEntity userProfile) {
        userProfileManager.deleteHard(userProfile);
    }

    public void deleteHard(UserPreferenceEntity userPreference) {
        userPreferenceManager.deleteHard(userPreference);
    }
}
