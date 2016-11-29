package com.receiptofi.domain;

import com.receiptofi.domain.types.CardNetworkEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 8/30/16 2:16 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "PAYMENT_CARD")
@CompoundIndexes (value = {
        @CompoundIndex (name = "payment_card_idx", def = "{'RID': 1}", background = true),
        @CompoundIndex (name = "payment_card_cd_idx", def = "{'RID': 1, 'CD': 1}", unique = true, background = true)
})
public class PaymentCardEntity extends BaseEntity {
    @NotNull
    @Field ("RID")
    private String rid;

    @Field ("NM")
    private String cardName;

    @Field ("CN")
    private CardNetworkEnum cardNetwork;

    @Field ("CD")
    private String cardDigit;

    /** External computation. */
    @Field ("LU")
    private Date lastUsed;

    /** External computation. */
    @Field ("UC")
    private int usedCount;

    /** To keep bean happy. */
    public PaymentCardEntity() {
        super();
    }

    private PaymentCardEntity(String rid, String cardName, CardNetworkEnum cardNetwork, String cardDigit) {
        super();
        this.rid = rid;
        this.cardName = cardName;
        this.cardNetwork = cardNetwork;
        this.cardDigit = cardDigit;
    }

    public static PaymentCardEntity newInstance(String rid, CardNetworkEnum cardNetwork, String cardDigit) {
        return new PaymentCardEntity(rid, null, cardNetwork, cardDigit);
    }

    public static PaymentCardEntity newInstance(String rid, String cardName, CardNetworkEnum cardNetwork, String cardDigit) {
        return new PaymentCardEntity(rid, cardName, cardNetwork, cardDigit);
    }

    public String getRid() {
        return rid;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public CardNetworkEnum getCardNetwork() {
        return cardNetwork;
    }

    public void setCardNetwork(CardNetworkEnum cardNetwork) {
        this.cardNetwork = cardNetwork;
    }

    public String getCardDigit() {
        return cardDigit;
    }

    public void setCardDigit(String cardDigit) {
        this.cardDigit = cardDigit;
    }

    @Override
    public String toString() {
        return "PaymentCardEntity{" +
                "rid='" + rid + '\'' +
                ", cardName='" + cardName + '\'' +
                ", cardNetwork=" + cardNetwork +
                ", cardDigit='" + cardDigit + '\'' +
                ", lastUsed=" + lastUsed +
                ", usedCount=" + usedCount +
                '}';
    }
}
