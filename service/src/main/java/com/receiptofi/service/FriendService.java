package com.receiptofi.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.receiptofi.domain.FriendEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.json.JsonAwaitingAcceptance;
import com.receiptofi.domain.json.JsonFriend;
import com.receiptofi.domain.types.ConnectionTypeEnum;
import com.receiptofi.repository.FriendManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private static final Logger LOG = LoggerFactory.getLogger(FriendService.class);

    private final Cache<String, Map<String, JsonFriend>> friends;
    private FriendManager friendManager;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public FriendService(
            @Value ("${FriendService.friendCacheSize}")
            int friendCacheSize,

            @Value ("${FriendService.friendCachePeriod}")
            int friendCachePeriod,

            FriendManager friendManager,
            UserProfilePreferenceService userProfilePreferenceService
    ) {
        LOG.info("Cache friends for {} {}", friendCachePeriod, TimeUnit.MINUTES.name());

        /** Do not make static cache or @Value out of constructor as cache is not set until Constructor is called. */
        friends = CacheBuilder.newBuilder()
                .maximumSize(friendCacheSize)
                .expireAfterWrite(friendCachePeriod, TimeUnit.MINUTES)
                .build();

        this.friendManager = friendManager;
        this.userProfilePreferenceService = userProfilePreferenceService;
    }

    public void save(FriendEntity friend) {
        friendManager.save(friend);
    }

    public void deleteHard(String receiptUserId, String friendUserId) {
        friendManager.deleteHard(receiptUserId, friendUserId);
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

        for (String fid : friendIds) {
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(fid);
            if (null != userProfile) {
                userProfiles.add(userProfile);
            }
        }

        return userProfiles;
    }

    public List<JsonAwaitingAcceptance> getPendingConnections(String rid) {
        List<JsonAwaitingAcceptance> jsonAwaitingAcceptances = new ArrayList<>();

        List<FriendEntity> friends = friendManager.findPendingFriends(rid);
        for (FriendEntity friend : friends) {
            /** Find by FID. */
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(friend.getFriendUserId());
            JsonAwaitingAcceptance jsonAwaitingAcceptance = new JsonAwaitingAcceptance(friend, userProfile);
            jsonAwaitingAcceptances.add(jsonAwaitingAcceptance);
        }

        return jsonAwaitingAcceptances;
    }

    public List<JsonAwaitingAcceptance> getAwaitingConnections(String rid) {
        List<JsonAwaitingAcceptance> jsonAwaitingAcceptances = new ArrayList<>();

        List<FriendEntity> friends = friendManager.findAwaitingFriends(rid);
        for (FriendEntity friend : friends) {
            /** Find by RID. */
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(friend.getReceiptUserId());
            JsonAwaitingAcceptance jsonAwaitingAcceptance = new JsonAwaitingAcceptance(friend, userProfile);
            jsonAwaitingAcceptances.add(jsonAwaitingAcceptance);
        }

        return jsonAwaitingAcceptances;
    }

    /**
     * Used for social friends. Just identifies connection and not FRIEND or UN-FRIEND.
     *
     * @param receiptUserId
     * @param friendUserId
     * @return
     */
    public boolean hasConnection(String receiptUserId, String friendUserId) {
        return friendManager.hasConnection(receiptUserId, friendUserId);
    }

    /**
     * Check friends are connected before performing SPLIT receipt or SHARING coupons.
     *
     * @param receiptUserId
     * @param friendUserId
     * @return
     */
    public boolean isConnected(String receiptUserId, String friendUserId) {
        return friendManager.isConnected(receiptUserId, friendUserId);
    }

    /**
     * Find specific connection between two users.
     *
     * @param receiptUserId
     * @param friendUserId
     * @return
     */
    public FriendEntity getConnection(String receiptUserId, String friendUserId) {
        return friendManager.getConnection(receiptUserId, friendUserId);
    }

    public boolean updateResponse(String id, String authenticationKey, boolean acceptConnection, String rid) {
        boolean response = friendManager.updateResponse(id, authenticationKey, acceptConnection, rid);
        if (response) {
            /** Do a refresh on friends after changes. */
            Map<String, JsonFriend> updatedFriends = updateJsonFriends(rid);
            for (String fid : updatedFriends.keySet()) {
                /** Refresh newly approved connections. */
                updateJsonFriends(fid);
            }
        }
        return response;
    }

    public boolean cancelInvite(String id, String authenticationKey) {
        return friendManager.cancelInvite(id, authenticationKey);
    }

    /**
     * Unfriend performed by rid against email id of the user rid is trying to unfriend.
     *
     * @param receiptUserId rid is un-friends from email address sent
     * @param unfriendEmail email of the person who is being unfriend
     * @return
     */
    public boolean unfriend(String receiptUserId, String unfriendEmail) {
        UserProfileEntity userProfile = userProfilePreferenceService.findByEmail(unfriendEmail);
        return friendManager.unfriend(receiptUserId, userProfile.getReceiptUserId());
    }

    public Map<String, JsonFriend> getFriends(String rid) {
        Map<String, JsonFriend> jsonFriends = friends.getIfPresent(rid);
        if (null == jsonFriends) {
            jsonFriends = updateJsonFriends(rid);
        }

        return new HashMap<>(jsonFriends);
    }

    private Map<String, JsonFriend> updateJsonFriends(String rid) {
        List<UserProfileEntity> userProfiles = getActiveConnections(rid);

        Map<String, JsonFriend> jsonFriends = new LinkedHashMap<>();
        for (UserProfileEntity userProfile : userProfiles) {
            jsonFriends.put(userProfile.getReceiptUserId(), new JsonFriend(userProfile));
        }

        friends.put(rid, jsonFriends);

        /** Update connection of friends too. Avoid recursive as the connection could span across multiple records. */
        for (String fid : jsonFriends.keySet()) {

            userProfiles = getActiveConnections(fid);

            Map<String, JsonFriend> jsonFriendOfFriends = new LinkedHashMap<>();
            for (UserProfileEntity userProfile : userProfiles) {
                jsonFriendOfFriends.put(userProfile.getReceiptUserId(), new JsonFriend(userProfile));
            }

            friends.put(fid, jsonFriendOfFriends);
        }

        return jsonFriends;
    }

    public boolean updateConnection(String id, String auth, ConnectionTypeEnum connectionType, String rid) {
        switch (connectionType) {
            case A:
                /** Accept connection. */
                return updateResponse(id, auth, true, rid);
            case C:
                /** Cancel invitation to friend by removing AUTH id. */
                return cancelInvite(id, auth);
            case D:
                /** Decline connection. */
                return updateResponse(id, auth, false, rid);
            default:
                LOG.error("ConnectionType={} not defined", connectionType);
                throw new UnsupportedOperationException("ConnectionType not supported " + connectionType);
        }
    }

    boolean inviteAgain(String id, String authKey) {
        return friendManager.inviteAgain(id, authKey);
    }
}
