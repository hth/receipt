package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 4/19/15 3:22 PM
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
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Mobile
public class JsonBillingHistory {

    @JsonProperty ("bs")
    private String billedStatus;

    @JsonProperty ("bt")
    private String accountBillingType;

    @JsonProperty ("bm")
    private String billedForMonth;

    public JsonBillingHistory(BillingHistoryEntity billingHistory) {
        this.billedStatus = billingHistory.getBilledStatus().name();
        this.accountBillingType = billingHistory.getAccountBillingType().name();
        this.billedForMonth = billingHistory.getBilledForMonthYear();
    }
}
