package com.receiptofi.web.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.json.ExpenseTag;
import com.receiptofi.domain.json.Receipt;
import com.receiptofi.domain.json.ReceiptItem;

import java.util.List;

/**
 * User: hitender
 * Date: 12/29/14 9:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
public class JsonReceiptDetail {

    private Receipt receipt;
    private List<ReceiptItem> items;
    private List<ExpenseTag> expenseTags;

    private JsonReceiptDetail() {
    }

    private JsonReceiptDetail(Receipt receipt, List<ReceiptItem> items, List<ExpenseTag> expenseTags) {
        this.receipt = receipt;
        this.items = items;
        this.expenseTags = expenseTags;
    }

    public static JsonReceiptDetail newInstance(Receipt receipt, List<ReceiptItem> items, List<ExpenseTag> expenseTags) {
        return new JsonReceiptDetail(receipt, items, expenseTags);
    }
}
