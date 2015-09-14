package com.receiptofi.service;

import com.receiptofi.domain.FriendEntity;
import com.receiptofi.repository.FriendManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Autowired private FriendManager friendManager;

    public void save(FriendEntity friend) {
        friendManager.save(friend);
    }

    public List<FriendEntity> findFriends(String rid) {
        return friendManager.findFriends(rid);
    }

    public Set<String> combine(List<FriendEntity> friends) {
        Set<String> allFriends = new HashSet<>();
        for (FriendEntity friend : friends) {
            allFriends.add(friend.getFriendUserId());
            allFriends.add(friend.getReceiptUserId());
        }

        //TODO(hth) speed this up
        return allFriends;
    }
}
