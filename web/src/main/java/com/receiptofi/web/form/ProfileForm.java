package com.receiptofi.web.form;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.domain.value.DiskUsageGrouped;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
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
    @Size (min = 2, max = 32)
    @Pattern (regexp = "^[\\p{L} .'-]+$")
    private ScrubbedInput firstName;

    @Size (min = 0, max = 32)
    @Pattern (regexp = "^[\\p{L} .'-]+$")
    private ScrubbedInput lastName;

    @NotNull
    @Size (min = 5, max = 32)
    @Pattern (regexp = "^[^@]+@[^@]+\\.[^@]+$")
    private ScrubbedInput mail;
    private Date updated;
    private String profileImage;

    /** For Admin related tab. */
    private String rid;
    private UserLevelEnum level;
    private boolean active;

    /** Populate expense tag. */
    private List<ExpenseTagEntity> expenseTags;
    private Map<String, Long> expenseTagCount = new HashMap<>();

    private StringBuilder errorMessage = new StringBuilder();
    private StringBuilder successMessage = new StringBuilder();

    private Date accountValidationExpireDay;
    private boolean accountValidationExpired;
    private boolean accountValidated;

    private DiskUsageGrouped diskUsage;
    private long pendingDiskUsage;

    @SuppressWarnings ("unused")
    private ProfileForm() {
    }

    private ProfileForm(UserProfileEntity userProfileEntity) {
        firstName = new ScrubbedInput(userProfileEntity.getFirstName());
        lastName = new ScrubbedInput(userProfileEntity.getLastName());
        mail = new ScrubbedInput(userProfileEntity.getEmail());
        rid = userProfileEntity.getReceiptUserId();
        level = userProfileEntity.getLevel();
        active = userProfileEntity.isActive();
        updated = userProfileEntity.getUpdated();
    }

    public static ProfileForm newInstance(UserProfileEntity userProfileEntity) {
        return new ProfileForm(userProfileEntity);
    }

    public ScrubbedInput getFirstName() {
        return firstName;
    }

    public void setFirstName(ScrubbedInput firstName) {
        this.firstName = new ScrubbedInput(WordUtils.capitalize(firstName.getText()));
    }

    public ScrubbedInput getLastName() {
        return lastName;
    }

    public void setLastName(ScrubbedInput lastName) {
        this.lastName = new ScrubbedInput(WordUtils.capitalize(lastName.getText()));
    }

    public ScrubbedInput getMail() {
        return mail;
    }

    public void setMail(ScrubbedInput mail) {
        this.mail = mail;
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
        return errorMessage.toString();
    }

    public void setErrorMessage(String errorMessage) {
        if (StringUtils.isBlank(this.errorMessage)) {
            this.errorMessage.append(errorMessage);
        } else {
            this.errorMessage.append(" ").append(errorMessage);
        }
    }

    public String getSuccessMessage() {
        return successMessage.toString();
    }

    public void setSuccessMessage(String successMessage) {
        if (StringUtils.isBlank(this.successMessage)) {
            this.successMessage.append(successMessage);
        } else {
            this.successMessage.append(" ").append(successMessage);
        }
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

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public void setAccountValidated(boolean accountValidated) {
        this.accountValidated = accountValidated;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setDiskUsage(DiskUsageGrouped diskUsage) {
        this.diskUsage = diskUsage;
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public BigDecimal getTotalSLN_MB() {
        return Maths.divide(diskUsage.getTotalSLN(), DiskUsageGrouped.MB);
    }
    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public BigDecimal getPendingDiskUsage_MB() {
        return Maths.divide(pendingDiskUsage, DiskUsageGrouped.MB);
    }

    /**
     * Saved space result of file scaling.
     *
     * @return
     */
    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public BigDecimal getDiskSaved_MB() {
        return Maths.divide(diskUsage.getTotalLN() - pendingDiskUsage - diskUsage.getTotalSLN(), DiskUsageGrouped.MB);
    }

    public void setPendingDiskUsage(long pendingDiskUsage) {
        this.pendingDiskUsage = pendingDiskUsage;
    }
}
