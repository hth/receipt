package com.receiptofi.domain.json;

import static com.receiptofi.domain.json.JsonReceipt.ISO8601_FMT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.PaymentCardEntity;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.TimeZone;

/**
 * User: hitender
 * Date: 8/30/16 7:05 PM
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
public class JsonPaymentCard {

    @JsonProperty ("cn")
    private String cardNetwork;

    @JsonProperty ("cd")
    private String cardDigit;

    @JsonProperty ("lu")
    private String lastUsed;

    @JsonProperty ("uc")
    private int usedCount;

    @JsonProperty ("a")
    private boolean active;

    public JsonPaymentCard(PaymentCardEntity paymentCard) {
        this.cardNetwork = paymentCard.getCardNetwork().name();
        this.cardDigit = paymentCard.getCardDigit();
        this.lastUsed = DateFormatUtils.format(paymentCard.getLastUsed(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.usedCount = paymentCard.getUsedCount();
        this.active = paymentCard.isActive();
    }

    public String getCardNetwork() {
        return cardNetwork;
    }

    public String getCardDigit() {
        return cardDigit;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public boolean isActive() {
        return active;
    }
}
