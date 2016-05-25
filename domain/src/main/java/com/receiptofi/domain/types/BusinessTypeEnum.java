package com.receiptofi.domain.types;

import java.util.Arrays;
import java.util.List;

/**
 * User: hitender
 * Date: 12/12/15 5:08 PM
 */
public enum BusinessTypeEnum {
    RS("RS", "Restaurant"),
    BA("BA", "Bar"),
    ST("ST", "Store"),
    LD("LD", "Lodging"),
    SM("SM", "Shopping Mall"),
    MT("MT", "Movie Theater"),
    GA("GA", "Gas Station"),
    SC("SC", "School"),
    GS("GS", "Grocery Store"),
    CF("CF", "Cafe"),
    HO("HO", "Hospital"),
    DO("DO", "Doctor"),
    PH("PH", "Pharmacy"),
    PW("PW", "Place of Worship"),
    MU("MU", "Museum"),
    TA("TA", "Tourist Attraction"),
    NC("NC", "Night Club"),
    BK("BK", "Bank"),
    AT("AT", "ATM"),
    GY("GY", "GYM"),
    PA("PA", "Park");

    private final String description;
    private final String name;

    BusinessTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<BusinessTypeEnum> asList() {
        BusinessTypeEnum[] all = BusinessTypeEnum.values();
        return Arrays.asList(all);
    }

    @Override
    public String toString() {
        return this.description;
    }
}
