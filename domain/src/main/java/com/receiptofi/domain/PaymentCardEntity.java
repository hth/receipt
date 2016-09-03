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

    @Field ("CN")
    private CardNetworkEnum cardNetwork;

    @Field ("CD")
    private String cardDigit;

    @Field ("LU")
    private Date lastUsed;

    @Field ("UC")
    private int usedCount;

    private PaymentCardEntity(String rid, CardNetworkEnum cardNetwork, String cardDigit, Date lastUsed) {
        this.rid = rid;
        this.cardNetwork = cardNetwork;
        this.cardDigit = cardDigit;
        this.lastUsed = lastUsed;
    }

    public static PaymentCardEntity newInstance(String rid, CardNetworkEnum cardNetwork, String cardDigit, Date lastUsed) {
        return new PaymentCardEntity(rid, cardNetwork, cardDigit, lastUsed);
    }

    public String getRid() {
        return rid;
    }

    public CardNetworkEnum getCardNetwork() {
        return cardNetwork;
    }

    public String getCardDigit() {
        return cardDigit;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getUsedCount() {
        return usedCount;
    }
}
