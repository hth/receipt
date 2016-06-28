package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/15/16 7:07 PM
 */
public enum BusinessCampaignStatusEnum {
    N("N", "Not Complete"),
    P("P", "Pending Approval"),
    A("A", "Approved"),
    D("D", "Declined"), //Declined campaign can be modified and resubmitted to pending approval
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
