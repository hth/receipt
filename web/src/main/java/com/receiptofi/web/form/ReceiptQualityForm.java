package com.receiptofi.web.form;

import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 5/3/16 12:32 AM
 */
public class ReceiptQualityForm {
    private List<ReceiptAndItems> receiptAndItems = new LinkedList<>();

    /** Used for showing error messages to user when the request action fails to execute */
    String errorMessage;

    private ReceiptQualityForm() {
    }

    public static ReceiptQualityForm newInstance() {
        return new ReceiptQualityForm();
    }

    public List<ReceiptAndItems> getReceiptAndItems() {
        return receiptAndItems;
    }

    public void setReceiptAndItems(ReceiptEntity receipt, List<ItemEntity> items) {
        this.receiptAndItems.add(new ReceiptAndItems(receipt, items));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public class ReceiptAndItems {
        private ReceiptEntity receipt;
        private List<ItemEntity> items;

        private ReceiptAndItems() {}

        public ReceiptAndItems(ReceiptEntity receipt, List<ItemEntity> items) {
            this.receipt = receipt;
            this.items = items;
        }

        public ReceiptEntity getReceipt() {
            return receipt;
        }

        public List<ItemEntity> getItems() {
            return items;
        }
    }
}
