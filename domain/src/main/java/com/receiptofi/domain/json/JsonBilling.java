package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

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
    private static final Logger LOG = LoggerFactory.getLogger(JsonBilling.class);

    @JsonProperty ("bt")
    private String billingPlan;

    @JsonProperty ("billingHistories")
    private List<JsonBillingHistory> billingHistories = new LinkedList<>();

    public JsonBilling(BillingAccountEntity billingAccount, List<BillingHistoryEntity> billings) {
        if (billingAccount == null) {
            Assert.notEmpty(billings, "Billing Histories are empty");
            LOG.error("Billing Account is null for rid=", billings.get(0).getRid());
            throw new RuntimeException("Billing Account is empty for rid=" + billings.get(0).getRid());
        } else {
            this.billingPlan = billingAccount.getBillingPlan().getDescription();
        }
        this.billingHistories.addAll(billings.stream().map(JsonBillingHistory::new).collect(Collectors.toList()));
    }
}
