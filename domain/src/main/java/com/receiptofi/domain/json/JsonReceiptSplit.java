package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.annotation.Mobile;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 10/19/15 1:09 AM
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
//@JsonInclude (JsonInclude.Include.NON_NULL)
@Mobile
public class JsonReceiptSplit {
    @JsonProperty ("receiptId")
    private String receiptId;

    @JsonProperty ("splits")
    private List<JsonFriend> splits = new ArrayList<>();

    public String getReceiptId() {
        return receiptId;
    }

    public JsonReceiptSplit setReceiptId(String receiptId) {
        this.receiptId = receiptId;
        return this;
    }

    public List<JsonFriend> getSplits() {
        return splits;
    }

    public JsonReceiptSplit setSplits(List<JsonFriend> splits) {
        this.splits = splits;
        return this;
    }

    public JsonReceiptSplit addSplit(JsonFriend jsonFriend) {
        this.splits.add(jsonFriend);
        return this;
    }
}
