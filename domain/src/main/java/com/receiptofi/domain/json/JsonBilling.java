package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.BillingPlanEnum;

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
    private String billingPlan;

    @JsonProperty ("billingHistories")
    private List<JsonBillingHistory> billingHistories = new LinkedList<>();

    public JsonBilling(BillingAccountEntity billingAccount, List<BillingHistoryEntity> billings) {
        //TODO(hth) fix this issue as there should be a default NB status for all account
        /** BillingAccount can be inactive when user has un-subscribed. */
        if (billingAccount == null) {
            this.billingPlan = BillingPlanEnum.NB.name();
        } else {
            this.billingPlan = billingAccount.getBillingPlan().name();
        }
        this.billingHistories.addAll(billings.stream().map(JsonBillingHistory::new).collect(Collectors.toList()));
    }
}
