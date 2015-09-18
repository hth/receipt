package com.receiptofi.service;

import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.json.JsonAwaitingAcceptance;
import com.receiptofi.repository.FriendManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 9/13/15 10:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FriendService {
    public static final String ACTIVE = "ACTIVE";
    public static final String PENDING = "PENDING";

    @Autowired private FriendManager friendManager;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

    public void save(FriendEntity friend) {
        friendManager.save(friend);
    }

    public void deleteHard(String receiptUserId, String friendUserId) {
        friendManager.deleteHard(receiptUserId, friendUserId);
    }

    public Map<String, List<UserProfileEntity>> getProfileForAllFriends(String rid) {
        Map<String, List<UserProfileEntity>> profiles = new HashMap<>();
        profiles.put(ACTIVE, getActiveConnections(rid));
        profiles.put(PENDING, getPendingConnections(rid));

        return profiles;
    }

    public List<FriendEntity> findConnections(String rid) {
        return friendManager.findConnections(rid);
    }

    public List<UserProfileEntity> getActiveConnections(String rid) {
        List<UserProfileEntity> userProfiles = new ArrayList<>();

        Set<String> friendIds = new HashSet<>();
        List<FriendEntity> friends = friendManager.findFriends(rid);
        for (FriendEntity friend : friends) {
            friendIds.add(friend.getReceiptUserId());
            friendIds.add(friend.getFriendUserId());
        }
        friendIds.remove(rid);

        for (String id : friendIds) {
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(id);
            if (null != userProfile) {
                userProfiles.add(userProfile);
            }
        }

        return userProfiles;
    }

    public List<UserProfileEntity> getPendingConnections(String rid) {
        List<UserProfileEntity> userProfiles = new ArrayList<>();

        List<FriendEntity> friends = friendManager.findPendingFriends(rid);
        userProfiles.addAll(friends.stream().map(friend -> userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(friend.getFriendUserId())).collect(Collectors.toList()));

        return userProfiles;
    }

    public List<JsonAwaitingAcceptance> getAwaitingConnections(String rid) {
        List<JsonAwaitingAcceptance> jsonAwaitingAcceptances = new ArrayList<>();

        List<FriendEntity> friends = friendManager.findAwaitingFriends(rid);
        for (FriendEntity friend : friends) {
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(friend.getReceiptUserId());
            JsonAwaitingAcceptance jsonAwaitingAcceptance = new JsonAwaitingAcceptance(friend, userProfile);
            jsonAwaitingAcceptances.add(jsonAwaitingAcceptance);
        }

        return jsonAwaitingAcceptances;
    }

    public boolean hasConnection(String receiptUserId, String friendUserId) {
        return friendManager.hasConnection(receiptUserId, friendUserId);
    }

    public boolean updateResponse(String id, String authenticationKey, boolean acceptConnection, String rid) {
        return friendManager.updateResponse(id, authenticationKey, acceptConnection, rid);
    }
}
