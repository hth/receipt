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
     * @param receiptUserId
     * @param friendUserId
     * @return
     */
    boolean hasConnection(String receiptUserId, String friendUserId);

    void deleteHard(String receiptUserId, String friendUserId);

    boolean updateResponse(String id, String authenticationKey, boolean acceptConnection, String rid);
}
