package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

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
    private static final Logger LOG = LoggerFactory.getLogger(JsonBillingHistory.class);

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("bs")
    private String billedStatus;

    @JsonProperty ("bt")
    private String billingPlan;

    @JsonProperty ("bm")
    private String billedForMonth;

    @JsonProperty ("ts")
    private String transactionStatus;

    @JsonProperty ("bd")
    private String billedDate;

    public JsonBillingHistory(BillingHistoryEntity billingHistory) {
        this.id = billingHistory.getId();
        this.billedStatus = billingHistory.getBilledStatus().name();
        if (billingHistory.getBillingPlan() != null) {
            this.billingPlan = billingHistory.getBillingPlan().getDescription();
        } else {
            LOG.warn("Billing plan is empty rid={} id={}", billingHistory.getRid(), billingHistory.getId());
            this.billingPlan = "";
        }
        this.billedForMonth = billingHistory.getBilledForMonth();
        this.transactionStatus = billingHistory.getTransactionStatus().name();
        this.billedDate = DateFormatUtils.format(billingHistory.getUpdated(), JsonReceipt.ISO8601_FMT, TimeZone.getTimeZone("UTC"));
    }
}
