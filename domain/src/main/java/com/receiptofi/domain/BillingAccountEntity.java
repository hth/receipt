package com.receiptofi.domain;

import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.types.PaymentGatewayEnum;

import org.springframework.data.annotation.Transient;
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
@CompoundIndexes ({
        @CompoundIndex (name = "billing_account_idx", def = "{'RID': -1}", background = true),
        @CompoundIndex (name = "billing_account_sub_idx", def = "{'SD': -1}", background = true),
})
public class BillingAccountEntity extends BaseEntity {

    @Field ("RID")
    private String rid;

    /** Defaults to Promotional to begin with. */
    @Field ("BP")
    private BillingPlanEnum billingPlan = BillingPlanEnum.P;

    @Field ("PG")
    private PaymentGatewayEnum paymentGateway;

    @Field ("CD")
    private String customerId;

    @Field ("FN")
    private String firstName;

    @Field ("LN")
    private String lastName;

    @Field ("CM")
    private String company;

    @Field ("AD")
    private String addressId;

    @Field ("PC")
    private String postalCode;

    @Field ("SD")
    private String subscriptionId;

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

    public PaymentGatewayEnum getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGatewayEnum paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Transient
    public String getName() {
        return firstName + " " + lastName;
    }
}
