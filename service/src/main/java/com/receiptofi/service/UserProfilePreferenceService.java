package com.receiptofi.service;

import java.util.Date;
import java.util.List;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.repository.ExpenseTagManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;

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
    @Autowired private ExpenseTagManager expenseTagManager;

    public UserProfileEntity findByEmail(String email) {
        return userProfileManager.findByEmail(email);
    }

    public UserProfileEntity findByReceiptUserId(String receiptUserId) {
        return userProfileManager.findByReceiptUserId(receiptUserId);
    }

    public UserProfileEntity forProfilePreferenceFindByReceiptUserId(String receiptUserId) {
        return userProfileManager.forProfilePreferenceFindByReceiptUserId(receiptUserId);
    }

    public UserProfileEntity findByUserId(String email) {
        return userProfileManager.findByUserId(email);
    }

    public void updateProfile(UserProfileEntity userProfile) throws Exception {
        userProfileManager.save(userProfile);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    @Mobile
    @SuppressWarnings("unused")
    public UserProfileEntity getProfileUpdateSince(String rid, Date since) {
        return userProfileManager.getProfileUpdateSince(rid, since);
    }

    /**
     * Lists all the expenseTypes
     *
     * @param userProfileId
     * @return
     */
    public List<ExpenseTagEntity> allExpenseTypes(String userProfileId) {
        return expenseTagManager.allExpenseTypes(userProfileId);
    }

    /**
     * Lists all the active expense types
     *
     * @param userProfileId
     * @return
     */
    public List<ExpenseTagEntity> activeExpenseTypes(String userProfileId) {
        return expenseTagManager.activeExpenseTypes(userProfileId);
    }

    public ExpenseTagEntity getExpenseType(String expenseTypeId) {
        return expenseTagManager.findOne(expenseTypeId);
    }

    public void addExpenseType(ExpenseTagEntity expenseType) {
        expenseTagManager.save(expenseType);
    }

    public void modifyVisibilityOfExpenseType(String expenseTypeId, String changeStatTo, String receiptUserId) {
        if(changeStatTo.equalsIgnoreCase("true")) {
            expenseTagManager.changeVisibility(expenseTypeId, false, receiptUserId);
        } else {
            expenseTagManager.changeVisibility(expenseTypeId, true, receiptUserId);
        }
    }
}
