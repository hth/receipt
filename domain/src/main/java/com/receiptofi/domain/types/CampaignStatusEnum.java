package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/15/16 7:07 PM
 */
public enum CampaignStatusEnum {
    N("N", "In Progress. Confirm to mark as complete."),
    P("P", "Pending Approval"),
    /** Declined campaign can be modified and resubmitted to pending approval. */
    D("D", "Declined"),
    A("A", "Approved"),
    /** State after APPROVED and before making system LIVE. */
    S("S", "Campaign set to go live"),
    L("L", "Live"),
    E("E", "Campaign Ended");

    private final String description;
    private final String name;

    CampaignStatusEnum(String name, String description) {
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
