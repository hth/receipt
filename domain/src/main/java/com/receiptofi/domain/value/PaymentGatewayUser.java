package com.receiptofi.domain.value;

import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.PaymentGatewayEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field ("AD")
    private String addressId;

    @Field ("PC")
    private String postalCode;

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

    @Transient
    public String getName() {
        return firstName + " " + lastName;
    }
}
