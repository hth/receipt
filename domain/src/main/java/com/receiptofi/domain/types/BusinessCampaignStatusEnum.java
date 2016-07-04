package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/15/16 7:07 PM
 */
public enum BusinessCampaignStatusEnum {
    N("N", "In Progress. Confirm to mark as complete."),
    P("P", "Pending Approval"),
    A("A", "Approved"),
    /** Declined campaign can be modified and resubmitted to pending approval. */
    D("D", "Declined"),
    /** State after APPROVED and before making system LIVE. */
    S("S", "Campaign set to go live"),
    L("L", "Live"),
    E("E", "End Campaign");

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
        return description;
    }
}
