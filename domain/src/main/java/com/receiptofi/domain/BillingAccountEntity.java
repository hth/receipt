package com.receiptofi.domain;

import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.value.PaymentGatewayUser;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;

/**
 * Each users current billing status.
 * User: hitender
 * Date: 3/19/15 2:56 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BILLING_ACCOUNT")
@CompoundIndexes ({
        @CompoundIndex (name = "billing_account_idx", def = "{'RID': -1}", unique = true, background = true),
        @CompoundIndex (name = "billing_account_sub_idx", def = "{'PGU.SD': -1, 'PGU.PG': -1, 'U' : -1}", unique = true, background = true)
})
public class BillingAccountEntity extends BaseEntity {

    @Field ("RID")
    private String rid;

    /** Defaults to P to begin with. */
    @Field ("ABT")
    private BillingPlanEnum billingPlan = BillingPlanEnum.P;

    /**
     * PaymentGateway details.
     */
    @Field ("PGU")
    private LinkedList<PaymentGatewayUser> paymentGateway = new LinkedList<>();

    @SuppressWarnings ("unused")
    private BillingAccountEntity() {
        super();
    }

    public BillingAccountEntity(String rid) {
        super();
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }

    public BillingPlanEnum getBillingPlan() {
        return billingPlan;
    }

    public void setBillingPlan(BillingPlanEnum billingPlan) {
        this.billingPlan = billingPlan;
    }

    public LinkedList<PaymentGatewayUser> getPaymentGateway() {
        return paymentGateway;
    }

    public void addPaymentGateway(PaymentGatewayUser paymentGateway) {
        this.paymentGateway.add(paymentGateway);
    }
}
