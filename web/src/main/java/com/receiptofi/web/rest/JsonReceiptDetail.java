package com.receiptofi.web.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.json.JsonExpenseTag;
import com.receiptofi.domain.json.JsonReceipt;
import com.receiptofi.domain.json.JsonReceiptItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 12/29/14 9:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonInclude (JsonInclude.Include.NON_NULL)
public class JsonReceiptDetail {

    @JsonProperty ("receipt")
    private JsonReceipt jsonReceipt = new JsonReceipt();

    @JsonProperty ("items")
    private List<JsonReceiptItem> items = new LinkedList<>();

    @JsonProperty ("expenseTags")
    private List<JsonExpenseTag> jsonExpenseTags = new ArrayList<>();

    public JsonReceiptDetail() {
    }

    public void setJsonReceipt(JsonReceipt jsonReceipt) {
        this.jsonReceipt = jsonReceipt;
    }

    public void setItems(List<JsonReceiptItem> items) {
        this.items = items;
    }

    public void setJsonExpenseTags(List<JsonExpenseTag> jsonExpenseTags) {
        this.jsonExpenseTags = jsonExpenseTags;
    }
}
