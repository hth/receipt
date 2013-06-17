package com.tholix.web.form;

import java.util.List;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;

/**
 * User: hitender
 * Date: 5/24/13
 * Time: 1:20 AM
 */
public final class ExpenseForm {

    String name;
    List<ExpenseTypeEntity> expenseTypes;
    List<ItemEntity> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExpenseTypeEntity> getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(List<ExpenseTypeEntity> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }
}
