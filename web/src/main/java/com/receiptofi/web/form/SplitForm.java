package com.receiptofi.web.form;

import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.json.JsonAwaitingAcceptance;
import com.receiptofi.domain.json.JsonOweExpenses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 9/14/15 9:16 PM
 */
public class SplitForm {
    /** List of friends, request. */
    private List<UserProfileEntity> activeProfiles;
    private List<JsonAwaitingAcceptance> awaitingProfiles;
    private List<JsonAwaitingAcceptance> pendingProfiles;

    /** For showing chart data. */
    private List<JsonOweExpenses> jsonOweMe;
    private List<JsonOweExpenses> jsonOweOthers;

    /** For showing tabular data to settle split. */
    private Map<String, List<SplitExpensesEntity>> yourSplitExpenses = new HashMap<>();
    private Map<String, List<SplitExpensesEntity>> friendsSplitExpenses = new HashMap<>();

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

    public Map<String, List<SplitExpensesEntity>> getYourSplitExpenses() {
        return yourSplitExpenses;
    }

    public void addYourSplitExpenses(String id, List<SplitExpensesEntity> yourSplitExpenses) {
        this.yourSplitExpenses.put(id, yourSplitExpenses);
    }

    public Map<String, List<SplitExpensesEntity>> getFriendsSplitExpenses() {
        return friendsSplitExpenses;
    }

    public void addFriendsSplitExpenses(String id, List<SplitExpensesEntity> friendsSplitExpenses) {
        this.friendsSplitExpenses.put(id, friendsSplitExpenses);
    }

    /**
     * Find if friends owes user. If does, then show the settle button.
     *
     * @param rid
     * @return
     */
    public boolean canBeSettledWithFriend(String rid) {
        for (UserProfileEntity userProfile : activeProfiles) {
            if (userProfile.getReceiptUserId().equals(rid)) {
                return this.friendsSplitExpenses.get(userProfile.getName()) != null;
            }
        }
        return false;
    }
}
