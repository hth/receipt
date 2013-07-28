package com.tholix.web.form;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 7/26/13
 * Time: 7:17 PM
 */
public final class ExpenseTypeForm {
    private String expName;

    public static ExpenseTypeForm newInstance() {
        return new ExpenseTypeForm();
    }

    public String getExpName() {
        return expName;
    }

    public void setExpName(String expName) {
        this.expName = StringUtils.trim(expName);
    }
}
