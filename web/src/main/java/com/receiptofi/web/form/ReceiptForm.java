package com.receiptofi.web.form;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 5/16/13
 * Time: 10:02 PM
 */
public final class ReceiptForm {

    private ReceiptEntity receipt;
    private List<ItemEntity> items;
    private List<ExpenseTagEntity> expenseTags;

    /** Used for showing error messages to user when the request action fails to execute */
    private String errorMessage;

    /** Need for bean instantiation */
    private ReceiptForm() {
    }

    public static ReceiptForm newInstance(ReceiptEntity receipt, List<ItemEntity> items, List<ExpenseTagEntity> expenseTypes) {
        ReceiptForm receiptForm = new ReceiptForm();
        receiptForm.setReceipt(receipt);
        receiptForm.setItems(items);
        receiptForm.setExpenseTags(expenseTypes);
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

    public List<ExpenseTagEntity> getExpenseTags() {
        return expenseTags;
    }

    public void setExpenseTags(List<ExpenseTagEntity> expenseTags) {
        this.expenseTags = expenseTags;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ReceiptForm{" +
                "receipt=" + receipt +
                ", items=" + items +
                ", expenseTags=" + expenseTags +
                '}';
    }
}
