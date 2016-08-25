package com.receiptofi.domain.types;

/**
 * User: hitender
 * Date: 6/8/16 10:33 PM
 */
public enum CampaignTypeEnum {
    /** To your patrons. */
    P("P", "Promote to patrons"),
    /** Promote to not your patrons. */
    NP("NP", "Promote to non patrons");

    private final String description;
    private final String name;

    CampaignTypeEnum(String name, String description) {
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
