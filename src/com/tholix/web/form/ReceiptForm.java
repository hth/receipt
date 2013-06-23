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
public final class ReceiptForm {

    ReceiptEntity receipt;
    List<ItemEntity> items;
    List<ExpenseTypeEntity> expenseTypes;

    /** Used for showing error messages to user when the request action fails to execute */
    String errorMessage;

    /**
     * Need for bean instantiation
     */
    private ReceiptForm() {}

    public static ReceiptForm newInstance(ReceiptEntity receipt, List<ItemEntity> items, List<ExpenseTypeEntity> expenseTypes) {
        ReceiptForm receiptForm = new ReceiptForm();
        receiptForm.setReceipt(receipt);
        receiptForm.setItems(items);
        receiptForm.setExpenseTypes(expenseTypes);
        return receiptForm;
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

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
