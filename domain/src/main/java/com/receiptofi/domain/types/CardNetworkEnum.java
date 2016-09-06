package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 8/30/16 2:21 PM
 */
public enum CardNetworkEnum {
    U("U", "-- Select --"),
    A("A", "American Express"),
    D("D", "Discover"),
    M("M", "MasterCard"),
    V("V", "Visa"),
    C("C", "Debit Card");

    private final String description;
    private final String name;

    CardNetworkEnum(String name, String description) {
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
        return description;
    }
}
