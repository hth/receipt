package com.receiptofi.repository;

import com.receiptofi.domain.FriendEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 9/13/15 10:10 PM
 */

public interface FriendManager extends RepositoryManager<FriendEntity> {
    List<FriendEntity> findConnections(String rid);

    /**
     * Friends are always connected.
     *
     * @param rid
     * @return
     */
    List<FriendEntity> findFriends(String rid);

    /**
     * Pending friends are friends who are suppose to approve your approval.
     *
     * @param rid
     * @return
     */
    List<FriendEntity> findPendingFriends(String rid);

    /**
     * Friends awaiting approvals from you.
     *
     * @param rid
     * @return
     */
    List<FriendEntity> findAwaitingFriends(String rid);

    /**
     * Has some form of connection. Accepted or awaiting acceptance. Or even unfriend.
     *
     * @param rid
     * @param fid
     * @return
     */
    boolean hasConnection(String rid, String fid);

    boolean isConnected(String rid, String fid);

    void deleteHard(String rid, String fid);

    boolean updateResponse(String id, String authenticationKey, boolean acceptConnection, String rid);

    boolean cancelInvite(String id, String authenticationKey);

    FriendEntity getConnection(String rid, String fid);

    boolean unfriend(String rid, String fid);

    /**
     * Re-invite friend after cancelling the invitation.
     *
     * @param id
     * @param authKey
     * @return
     */
    boolean inviteAgain(String id, String authKey);
}
