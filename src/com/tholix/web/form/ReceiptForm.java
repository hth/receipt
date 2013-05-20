package com.tholix.web.form;

import java.util.List;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;

/**
 * User: hitender
 * Date: 5/16/13
 * Time: 10:02 PM
 */
public class ReceiptForm {
    ReceiptEntity receipt;
    List<ItemEntity> items;
    List<ExpenseTypeEntity> expenseTypes;

    /**
     * Need for bean instantiation
     */
    private ReceiptForm() {

    }

    private ReceiptForm(ReceiptEntity receipt, List<ItemEntity> items, List<ExpenseTypeEntity> expenseTypes) {
        this.receipt = receipt;
        this.items = items;
        this.expenseTypes = expenseTypes;
    }

    public static ReceiptForm newInstance(ReceiptEntity receipt, List<ItemEntity> items, List<ExpenseTypeEntity> expenseTypes) {
        return new ReceiptForm(receipt, items, expenseTypes);
    }

    public ReceiptEntity getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptEntity receipt) {
        this.receipt = receipt;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    public List<ExpenseTypeEntity> getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(List<ExpenseTypeEntity> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }
}
