package com.receiptofi.domain.value;

import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.PaymentGatewayEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * User: hitender
 * Date: 5/10/15 8:10 PM
 */
@Mobile
public class PaymentGatewayUser {
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

    @Field ("C")
    private Date created;

    @Field ("U")
    private Date updated;

    public PaymentGatewayUser() {
        this.created = new Date();
        this.updated = this.created;
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

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Transient
    public String getName() {
        return firstName + " " + lastName;
    }
}
