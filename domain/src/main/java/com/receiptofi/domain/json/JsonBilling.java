package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.annotation.Mobile;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 4/19/15 3:12 PM
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
public class JsonBilling {

    @JsonProperty ("bt")
    private String accountBillingType;

    @JsonProperty ("billingHistories")
    private List<JsonBillingHistory> billingHistories = new LinkedList<>();

    public JsonBilling(BillingAccountEntity billingAccount, List<BillingHistoryEntity> billings) {
        this.accountBillingType = billingAccount.getAccountBillingType().name();
        this.billingHistories.addAll(billings.stream().map(JsonBillingHistory::new).collect(Collectors.toList()));
    }
}
