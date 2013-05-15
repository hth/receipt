package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.ExpenseTypeManager;
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
    @Autowired private ExpenseTypeManager expenseTypeManager;

    public UserProfileEntity loadFromEmail(String emailId) {
        return userProfileManager.getObjectUsingEmail(emailId);
    }

    public void updateProfile(UserProfileEntity userProfile) {
        userProfileManager.updateObject(userProfile.getId(), userProfile.getLevel());
    }

    public UserProfileEntity findById(String userProfileId) {
        return userProfileManager.findOne(userProfileId);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    public List<ExpenseTypeEntity> allExpenseTypes(String userProfileId) {
        return expenseTypeManager.allExpenseTypes(userProfileId);
    }

    public void addExpenseType(ExpenseTypeEntity expenseType) throws Exception {
        try {
            expenseTypeManager.save(expenseType);
        } catch (Exception e) {
            throw e;
        }
    }

    public void modifyVisibilityOfExpenseType(String expenseTypeId, String changeStatTo) {
        if(changeStatTo.equalsIgnoreCase("true")) {
            expenseTypeManager.changeVisibility(expenseTypeId, false);
        } else {
            expenseTypeManager.changeVisibility(expenseTypeId, true);
        }
    }
}
