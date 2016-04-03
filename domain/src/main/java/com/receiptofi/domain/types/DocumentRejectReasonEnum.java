package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 9/12/15 1:50 PM
 */
public enum DocumentRejectReasonEnum {
    C("C", "Not clear", "is not clear or the image is not sharp."),
    D("D", "Duplicate", "is a duplicate and a similar receipt already exists."),
    E("E", "Not English", "is not in English."),
    G("G", "Good Document", "is not Rejected."),
    M("M", "Missing required data", "is missing required data like business name, address, details of receipts."),
    V("V", "Not valid", "is not a valid receipt.");

    private final String name;
    private final String description;
    private final String inSentence;

    DocumentRejectReasonEnum(String name, String description, String inSentence) {
        this.name = name;
        this.description = description;
        this.inSentence = inSentence;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getInSentence() {
        return inSentence;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
