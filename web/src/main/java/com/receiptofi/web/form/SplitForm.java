package com.receiptofi.web.form;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.json.JsonAwaitingAcceptance;

import java.util.List;

/**
 * User: hitender
 * Date: 9/14/15 9:16 PM
 */
public class SplitForm {
    private List<UserProfileEntity> activeProfiles;
    private List<JsonAwaitingAcceptance> awaitingProfiles;
    private List<JsonAwaitingAcceptance> pendingProfiles;

    public List<UserProfileEntity> getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(List<UserProfileEntity> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public List<JsonAwaitingAcceptance> getAwaitingProfiles() {
        return awaitingProfiles;
    }

    public void setAwaitingProfiles(List<JsonAwaitingAcceptance> awaitingProfiles) {
        this.awaitingProfiles = awaitingProfiles;
    }

    public List<JsonAwaitingAcceptance> getPendingProfiles() {
        return pendingProfiles;
    }

    public void setPendingProfiles(List<JsonAwaitingAcceptance> pendingProfiles) {
        this.pendingProfiles = pendingProfiles;
    }
}
