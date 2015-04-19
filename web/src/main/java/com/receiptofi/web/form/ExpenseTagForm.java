package com.receiptofi.web.form;

import com.receiptofi.utils.ColorUtil;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.lang3.StringUtils;

/**
 * Used in adding new expense tag.
 * User: hitender
 * Date: 7/26/13
 * Time: 7:17 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ExpenseTagForm {
    private String tagName;
    private String tagColor;
    private String tagId;

    private StringBuilder errorMessage = new StringBuilder();
    private StringBuilder successMessage = new StringBuilder();

    private ExpenseTagForm() {
        this.tagColor = ColorUtil.getRandom();
    }

    public static ExpenseTagForm newInstance() {
        return new ExpenseTagForm();
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = new ScrubbedInput(tagName).getText().toUpperCase();
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = new ScrubbedInput(tagColor).getText().toUpperCase();
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = new ScrubbedInput(tagId).getText().toUpperCase();
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
}
