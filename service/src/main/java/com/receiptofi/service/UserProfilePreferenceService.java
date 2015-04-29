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

    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserPreferenceManager userPreferenceManager;

    public UserProfileEntity findByEmail(String email) {
        return userProfileManager.findByEmail(email);
    }

    public UserProfileEntity findByReceiptUserId(String receiptUserId) {
        return userProfileManager.findByReceiptUserId(receiptUserId);
    }

    public UserProfileEntity forProfilePreferenceFindByReceiptUserId(String receiptUserId) {
        return userProfileManager.forProfilePreferenceFindByReceiptUserId(receiptUserId);
    }

    public UserProfileEntity findByUserId(String uid) {
        return userProfileManager.findByUserId(uid);
    }

    public void updateProfile(UserProfileEntity userProfile) throws Exception {
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
