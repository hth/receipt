package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.PaymentCardEntity;

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

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("nm")
    private String cardName;

    @JsonProperty ("cn")
    private String cardNetwork;

    @JsonProperty ("cd")
    private String cardDigit;

    @JsonProperty ("a")
    private boolean active;

    public JsonPaymentCard(PaymentCardEntity paymentCard) {
        this.id = paymentCard.getId();
        this.cardName = paymentCard.getCardName();
        this.cardNetwork = paymentCard.getCardNetwork().name();
        this.cardDigit = paymentCard.getCardDigit();
        this.active = paymentCard.isActive();
    }

    public String getId() {
        return id;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardNetwork() {
        return cardNetwork;
    }

    public String getCardDigit() {
        return cardDigit;
    }

    public boolean isActive() {
        return active;
    }
}
