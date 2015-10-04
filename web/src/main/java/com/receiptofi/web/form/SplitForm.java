package com.receiptofi.web.form;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.json.JsonAwaitingAcceptance;
import com.receiptofi.domain.json.JsonOweExpenses;

import java.util.List;

/**
 * User: hitender
 * Date: 9/14/15 9:16 PM
 */
public class SplitForm {
    private List<UserProfileEntity> activeProfiles;
    private List<JsonAwaitingAcceptance> awaitingProfiles;
    private List<JsonAwaitingAcceptance> pendingProfiles;
    private List<JsonOweExpenses> jsonOweMe;
    private List<JsonOweExpenses> jsonOweOthers;

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

    public List<JsonOweExpenses> getJsonOweMe() {
        return jsonOweMe;
    }

    public void setJsonOweMe(List<JsonOweExpenses> jsonOweMe) {
        this.jsonOweMe = jsonOweMe;
    }

    public List<JsonOweExpenses> getJsonOweOthers() {
        return jsonOweOthers;
    }

    public void setJsonOweOthers(List<JsonOweExpenses> jsonOweOthers) {
        this.jsonOweOthers = jsonOweOthers;
    }
}
