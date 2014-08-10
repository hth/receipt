package com.receiptofi.domain;

import javax.validation.constraints.NotNull;

import java.util.Date;

import com.receiptofi.domain.types.RecentActivityEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mobile apps uses this data to find if there are any new updates on server before it starts fetching data.
 * User: hitender
 * Date: 8/9/14 2:55 PM
 */
@Document (collection = "RECENT_ACTIVITY")
@CompoundIndexes (value = {
        @CompoundIndex (name = "recent_activity_idx",    def = "{'RID': -1, 'RA': -1}", unique = true)
} )
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class RecentActivityEntity extends BaseEntity {

    @JsonProperty ("rid")
    @NotNull
    @Field ("RID")
    private String userProfileId;

    @JsonProperty ("updates")
    @NotNull
    @Field ("RA")
    private RecentActivityEnum recentActivity;

    @JsonProperty ("earliest")
    @NotNull
    @Field ("EL")
    private Date earliestUpdate;

    private RecentActivityEntity(String userProfileId, RecentActivityEnum recentActivity, Date earliestUpdate) {
        this.userProfileId = userProfileId;
        this.recentActivity = recentActivity;
        this.earliestUpdate = earliestUpdate;
    }

    /**
     * Saves recent changes made to user's account.
     * @param userProfileId
     * @param recentActivity
     * @param earliestUpdate - Date when record was updated, for receipt this would be receipt date instead
     * @return
     */
    public static RecentActivityEntity newInstance(String userProfileId, RecentActivityEnum recentActivity, Date earliestUpdate) {
        return new RecentActivityEntity(userProfileId, recentActivity, earliestUpdate);
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public RecentActivityEnum getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(RecentActivityEnum recentActivity) {
        this.recentActivity = recentActivity;
    }

    public Date getEarliestUpdate() {
        return earliestUpdate;
    }

    public void setEarliestUpdate(Date earliestUpdate) {
        this.earliestUpdate = earliestUpdate;
    }
}
