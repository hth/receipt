package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 10/3/15 3:23 AM
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
public class JsonOweExpenses {
    private String receiptUserId;
    private String friendUserId;
    private Double splitTotal;
    private String name;

    public JsonOweExpenses(String receiptUserId, String friendUserId, Double splitTotal, String name) {
        this.receiptUserId = receiptUserId;
        this.friendUserId = friendUserId;
        this.splitTotal = splitTotal;
        this.name = name;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public Double getSplitTotal() {
        return splitTotal;
    }

    public String getName() {
        return name;
    }
}
