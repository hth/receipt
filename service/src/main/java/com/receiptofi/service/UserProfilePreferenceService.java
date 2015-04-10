package com.receiptofi.service;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.repository.ExpenseTagManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    @SuppressWarnings ("unused")
    public UserProfileEntity getProfileUpdateSince(String rid, Date since) {
        return userProfileManager.getProfileUpdateSince(rid, since);
    }

    /**
     * Lists all the expenseTypes.
     *
     * @param rid
     * @return
     */
    public List<ExpenseTagEntity> getAllExpenseTypes(String rid) {
        return expenseTagManager.getAllExpenseTags(rid);
    }

    /**
     * Lists all the active expense types.
     *
     * @param rid
     * @return
     */
    public List<ExpenseTagEntity> getExpenseTags(String rid) {
        return expenseTagManager.getExpenseTags(rid);
    }

    public ExpenseTagEntity getExpenseTag(String rid, String expenseTypeId) {
        return expenseTagManager.getExpenseTag(rid, expenseTypeId);
    }

    public void saveExpenseTag(ExpenseTagEntity expenseType) {
        expenseTagManager.save(expenseType);
    }

    public void updateExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid) {
        expenseTagManager.updateExpenseTag(expenseTypeId, expenseTagName, expenseTagColor, rid);
    }

    public void deleteExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid) {
        expenseTagManager.deleteExpenseTag(expenseTypeId, expenseTagName, expenseTagColor, rid);
    }

    public void deleteHard(UserProfileEntity userProfile) {
        userProfileManager.deleteHard(userProfile);
    }

    public void deleteHard(UserPreferenceEntity userPreference) {
        userPreferenceManager.deleteHard(userPreference);
    }
}
