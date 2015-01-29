package com.receiptofi.web.form;

import com.receiptofi.utils.ColorUtil;

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
public final class ExpenseTypeForm {
    private String tagName;
    private String tagColor;

    private ExpenseTypeForm() {
        this.tagColor = ColorUtil.getRandom();
    }

    public static ExpenseTypeForm newInstance() {
        return new ExpenseTypeForm();
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = StringUtils.trim(tagName);
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = StringUtils.trim(tagColor);
    }
}
