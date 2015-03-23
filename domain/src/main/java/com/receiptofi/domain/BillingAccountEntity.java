package com.receiptofi.domain;

import com.receiptofi.domain.types.BillingAccountTypeEnum;

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
@CompoundIndexes ({@CompoundIndex (name = "billing_account_idx", def = "{'RID': 1}")})
public class BillingAccountEntity extends BaseEntity {

    @Field ("RID")
    private String rid;

    /** Defaults to PROMOTION to begin with. */
    @Field ("BAT")
    private BillingAccountTypeEnum accountBillingType = BillingAccountTypeEnum.PROMOTION;

    /**
     * This is true when at least one billing has been done with @BillingAccountTypeEnum set for this record. If user
     * changes their mind then this record will be marked as inactive and new @BillingAccountTypeEnum with new record
     * will be created.
     */
    @Field ("BA")
    private boolean billedAccount = false;

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

    public BillingAccountTypeEnum getAccountBillingType() {
        return accountBillingType;
    }

    public void setAccountBillingType(BillingAccountTypeEnum accountBillingType) {
        this.accountBillingType = accountBillingType;
    }

    public boolean isBilledAccount() {
        return billedAccount;
    }

    public void markAccountBilled() {
        this.billedAccount = true;
    }
}
