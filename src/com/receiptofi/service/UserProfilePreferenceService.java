package com.receiptofi.service;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ExpenseTypeManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 4/25/13
 * Time: 9:37 AM
 */
@Service
public final class UserProfilePreferenceService {

    @Autowired private UserProfileManager userProfileManager;
    @Autowired private UserPreferenceManager userPreferenceManager;
    @Autowired private ExpenseTypeManager expenseTypeManager;

    public UserProfileEntity loadFromEmail(String emailId) {
        return userProfileManager.getObjectUsingEmail(emailId);
    }

    public void updateProfile(UserProfileEntity userProfile) throws Exception {
        userProfileManager.save(userProfile);
    }

    public UserProfileEntity findById(String userProfileId) {
        return userProfileManager.findOne(userProfileId);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    /**
     * Lists all the expenseTypes
     *
     * @param userProfileId
     * @return
     */
    public List<ExpenseTagEntity> allExpenseTypes(String userProfileId) {
        return expenseTypeManager.allExpenseTypes(userProfileId);
    }

    /**
     * Lists all the active expense types
     *
     * @param userProfileId
     * @return
     */
    public List<ExpenseTagEntity> activeExpenseTypes(String userProfileId) {
        return expenseTypeManager.activeExpenseTypes(userProfileId);
    }

    public ExpenseTagEntity getExpenseType(String expenseTypeId) {
        return expenseTypeManager.findOne(expenseTypeId);
    }

    public void addExpenseType(ExpenseTagEntity expenseType) throws Exception {
        try {
            expenseTypeManager.save(expenseType);
        } catch (Exception e) {
            throw e;
        }
    }

    public void modifyVisibilityOfExpenseType(String expenseTypeId, String changeStatTo, String userProfileId) {
        if(changeStatTo.equalsIgnoreCase("true")) {
            expenseTypeManager.changeVisibility(expenseTypeId, false, userProfileId);
        } else {
            expenseTypeManager.changeVisibility(expenseTypeId, true, userProfileId);
        }
    }
}
