package com.receiptofi.web.form;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 5/3/16 12:32 AM
 */
public class ReceiptQualityForm {

    private List<ReceiptForm> receiptForms = new ArrayList<>();

    private ReceiptQualityForm() {
    }

    public static ReceiptQualityForm newInstance() {
        return new ReceiptQualityForm();
    }

    public List<ReceiptForm> getReceiptForms() {
        return receiptForms;
    }

    public void addReceiptForms(ReceiptForm receiptForm) {
        this.receiptForms.add(receiptForm);
    }
}
