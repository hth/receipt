package com.tholix.web.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 8/11/13
 * Time: 7:31 PM
 */
public final class UserProfilePreferenceForm {

    private UserProfileEntity userProfile;
    private UserPreferenceEntity userPreference;
    private List<ExpenseTypeEntity> expenseTypes;
    private Map<String, Long> expenseTypeCount = new HashMap<>();
    private int visibleExpenseTypes = 0;
    private boolean isActive = false;

    private String errorMessage;
    private String successMessage;

    private UserProfilePreferenceForm() {}

    public static UserProfilePreferenceForm newInstance() {
        return new UserProfilePreferenceForm();
    }

    public UserProfileEntity getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileEntity userProfile) {
        this.userProfile = userProfile;
        this.isActive = userProfile.isActive();
    }

    public UserPreferenceEntity getUserPreference() {
        return userPreference;
    }

    public void setUserPreference(UserPreferenceEntity userPreference) {
        this.userPreference = userPreference;
    }

    public List<ExpenseTypeEntity> getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(List<ExpenseTypeEntity> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }

    public Map<String, Long> getExpenseTypeCount() {
        return expenseTypeCount;
    }

    public void setExpenseTypeCount(Map<String, Long> expenseTypeCount) {
        this.expenseTypeCount = expenseTypeCount;
    }

    public int getVisibleExpenseTypes() {
        return visibleExpenseTypes;
    }

    public void setVisibleExpenseTypes(int visibleExpenseTypes) {
        this.visibleExpenseTypes = visibleExpenseTypes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        if(StringUtils.isEmpty(this.errorMessage))  {
            this.errorMessage = errorMessage;
        } else {
            this.errorMessage = this.errorMessage + ", " + errorMessage;
        }
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        if(StringUtils.isEmpty(this.successMessage)) {
            this.successMessage = successMessage;
        } else {
            this.successMessage = this.successMessage + ", " + successMessage;
        }
    }
}
