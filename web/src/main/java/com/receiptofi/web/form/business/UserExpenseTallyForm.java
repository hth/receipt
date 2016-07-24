package com.receiptofi.web.form.business;

import com.receiptofi.domain.ExpenseTallyEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 7/23/16 10:35 PM
 */
public class UserExpenseTallyForm {

    private List<ExpenseTallyEntity> expenseTallys;

    public List<ExpenseTallyEntity> getExpenseTallys() {
        return expenseTallys;
    }

    public void setExpenseTallys(List<ExpenseTallyEntity> expenseTallys) {
        this.expenseTallys = expenseTallys;
    }
}
