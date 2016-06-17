package com.receiptofi.domain.types;

/**
 * Captures the steps of transaction.
 * User: hitender
 * Date: 6/13/15 1:10 AM
 */
public enum TransactionStatusEnum {
    /** Initial status when billing history place holder is created. */
    N("N", "Not yet billed"),

    /** Pending when submitted for settlement. */
    P("P", "Pending"),

    /** Only pending transaction can be voided. */
    V("V", "Void"),

    /** When transaction is settled. */
    S("S", "Successful"),

    /** Refund can only occur when transaction is settled. */
    R("R", "Refund");

    private final String description;
    private final String name;

    TransactionStatusEnum(String name, String description) {
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
