package com.receiptofi.web.form;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.text.WordUtils;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * User: hitender
 * Date: 1/30/15 2:54 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class ProfileForm {
    /** For profile tab. */
    @NotNull
    @Size(min = 2, max = 32)
    @Pattern(regexp = "^[\\p{L} .'-]+$")
    private String firstName;

    @Size(min = 0, max = 32)
    @Pattern(regexp = "^[\\p{L} .'-]+$")
    private String lastName;

    @NotNull
    @Size(min = 5, max = 32)
    @Pattern(regexp = "^[^@]+@[^@]+\\.[^@]+$")
    private String mail;
    private Date updated;

    /** For Admin related tab. */
    private String rid;
    private UserLevelEnum level;
    private boolean active;

    /** Populate expense tag. */
    private List<ExpenseTagEntity> expenseTags;
    private Map<String, Long> expenseTagCount = new HashMap<>();

    private String errorMessage;
    private String successMessage;

    private Date accountValidationExpireDay;
    private boolean accountValidationExpired;

    @SuppressWarnings ("unused")
    private ProfileForm() {
    }

    private ProfileForm(UserProfileEntity userProfileEntity) {
        firstName = userProfileEntity.getFirstName();
        lastName = userProfileEntity.getLastName();
        mail = userProfileEntity.getEmail();
        rid = userProfileEntity.getReceiptUserId();
        level = userProfileEntity.getLevel();
        active = userProfileEntity.isActive();
        updated = userProfileEntity.getUpdated();
    }

    public static ProfileForm newInstance(UserProfileEntity userProfileEntity) {
        return new ProfileForm(userProfileEntity);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = WordUtils.capitalize(new ScrubbedInput(firstName).getText());
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = WordUtils.capitalize(new ScrubbedInput(lastName).getText());
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = new ScrubbedInput(mail).getText().toLowerCase();
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public UserLevelEnum getLevel() {
        return level;
    }

    public void setLevel(UserLevelEnum level) {
        this.level = level;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ExpenseTagEntity> getExpenseTags() {
        return expenseTags;
    }

    public void setExpenseTags(List<ExpenseTagEntity> expenseTags) {
        this.expenseTags = expenseTags;
    }

    public Map<String, Long> getExpenseTagCount() {
        return expenseTagCount;
    }

    public void setExpenseTagCount(Map<String, Long> expenseTagCount) {
        this.expenseTagCount = expenseTagCount;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public Date getAccountValidationExpireDay() {
        return DateUtil.toDateTime(accountValidationExpireDay).minusDays(1).toDate();
    }

    public void setAccountValidationExpireDay(Date accountValidationExpireDay) {
        this.accountValidationExpireDay = accountValidationExpireDay;
    }

    public boolean isAccountValidationExpired() {
        return accountValidationExpired;
    }

    public void setAccountValidationExpired(boolean accountValidationExpired) {
        this.accountValidationExpired = accountValidationExpired;
    }
}
