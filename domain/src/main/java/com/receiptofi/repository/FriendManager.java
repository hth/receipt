package com.receiptofi.repository;

import com.receiptofi.domain.FriendEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 9/13/15 10:10 PM
 */

public interface FriendManager extends RepositoryManager<FriendEntity> {
    List<FriendEntity> findFriends(String rid);
}
