package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/15/16 7:07 PM
 */
public enum BusinessCampaignStatusEnum {
    I("I", "Incomplete"),
    C("C", "Complete"),
    A("A", "Approved"),
    R("R", "Rejected"), //moves to incomplete
    L("L", "Live"),
    E("E", "Expired"),
    T("T", "Terminated");


    private final String description;
    private final String name;

    BusinessCampaignStatusEnum(String name, String description) {
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
