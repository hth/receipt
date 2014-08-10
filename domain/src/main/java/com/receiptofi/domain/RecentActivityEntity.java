package com.receiptofi.domain;

import javax.validation.constraints.NotNull;

import java.util.Date;

import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.RecentActivityEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Mobile apps uses this data to find if there are any new updates on server before it starts fetching data.
 * User: hitender
 * Date: 8/9/14 2:55 PM
 */
@Mobile
@Document (collection = "RECENT_ACTIVITY")
@CompoundIndexes (value = {
        @CompoundIndex (name = "recent_activity_idx",    def = "{'RID': -1, 'RA': -1}", unique = true)
} )
public class RecentActivityEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String userProfileId;

    @NotNull
    @Field ("RA")
    private RecentActivityEnum recentActivity;

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

    public RecentActivityEnum getRecentActivity() {
        return recentActivity;
    }

    public Date getEarliestUpdate() {
        return earliestUpdate;
    }

    public void setEarliestUpdate(Date earliestUpdate) {
        this.earliestUpdate = earliestUpdate;
    }
}
