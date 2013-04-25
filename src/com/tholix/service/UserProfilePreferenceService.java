package com.tholix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.UserPreferenceManager;
import com.tholix.repository.UserProfileManager;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 9:37 AM
 */
@Service
public class UserProfilePreferenceService {

    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserPreferenceManager userPreferenceManager;

    public UserProfileEntity loadFromEmail(String emailId) {
        return userProfileManager.getObjectUsingEmail(emailId);
    }

    public void updateProfile(UserProfileEntity userProfile) {
        userProfileManager.updateObject(userProfile.getId(), userProfile.getLevel());
    }

    public UserProfileEntity findById(String id) {
        return userProfileManager.findOne(id);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }
}
