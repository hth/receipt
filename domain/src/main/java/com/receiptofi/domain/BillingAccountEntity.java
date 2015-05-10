package com.receiptofi.domain;

import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.domain.types.PaymentGatewayProviderEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@CompoundIndexes ({@CompoundIndex (name = "billing_account_idx", def = "{'RID': 1}", unique = true)})
public class BillingAccountEntity extends BaseEntity {

    @Field ("RID")
    private String rid;

    /** Defaults to P to begin with. */
    @Field ("ABT")
    private AccountBillingTypeEnum accountBillingType = AccountBillingTypeEnum.P;

    @Field ("PG")
    private PaymentGatewayProviderEnum paymentGatewayProvider;

    /**
     * This is true when at least one billing has been done with @BillingAccountTypeEnum set for this record. If user
     * changes their mind then this field with be marked as false until next billing cycle.
     */
    @Field ("BA")
    private boolean billedAccount = false;

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

    public AccountBillingTypeEnum getAccountBillingType() {
        return accountBillingType;
    }

    public void setAccountBillingType(AccountBillingTypeEnum accountBillingType) {
        this.accountBillingType = accountBillingType;
    }

    public PaymentGatewayProviderEnum getPaymentGatewayProvider() {
        return paymentGatewayProvider;
    }

    public void setPaymentGatewayProvider(PaymentGatewayProviderEnum paymentGatewayProvider) {
        this.paymentGatewayProvider = paymentGatewayProvider;
    }

    public boolean isBilledAccount() {
        return billedAccount;
    }

    public void markAccountBilled() {
        this.billedAccount = true;
    }
}
