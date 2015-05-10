package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 5/10/15 2:06 AM
 */
public enum PaymentGatewayProviderEnum {
    BT("BT", "Braintree");

    private String name;
    private String description;

    PaymentGatewayProviderEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
