package com.receiptofi.web.form;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.json.JsonFriend;

import java.util.List;

/**
 * User: hitender
 * Date: 5/16/13
 * Time: 10:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ReceiptForm {

    private ReceiptEntity receipt;
    private List<ItemEntity> items;
    private List<ExpenseTagEntity> expenseTags;
    private List<JsonFriend> jsonFriends;

    /** Used for showing error messages to user when the request action fails to execute */
    private String errorMessage;

    /** Need for bean instantiation */
    @SuppressWarnings("unused")
    private ReceiptForm() {
    }

    private ReceiptForm(
            ReceiptEntity receipt,
            List<ItemEntity> items,
            List<ExpenseTagEntity> expenseTags,
            List<JsonFriend> jsonFriends) {
        this.receipt = receipt;
        this.items = items;
        this.expenseTags = expenseTags;
        this.jsonFriends = jsonFriends;
    }

    public static ReceiptForm newInstance(
            ReceiptEntity receipt,
            List<ItemEntity> items,
            List<ExpenseTagEntity> expenseTags,
            List<JsonFriend> jsonFriends) {
        return new ReceiptForm(receipt, items, expenseTags, jsonFriends);
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

    public List<JsonFriend> getJsonFriends() {
        return jsonFriends;
    }

    public void setJsonFriends(List<JsonFriend> jsonFriends) {
        this.jsonFriends = jsonFriends;
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
