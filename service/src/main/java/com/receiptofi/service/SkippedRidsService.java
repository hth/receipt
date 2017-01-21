package com.receiptofi.service;

import com.receiptofi.domain.GenerateUserIds;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.repository.GenerateUserIdManager;
import com.receiptofi.repository.UserAccountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Auto correct when some RIDs are skipped.
 * User: hitender
 * Date: 8/13/16 3:22 PM
 */
@Service
public class SkippedRidsService {
    private static final Logger LOG = LoggerFactory.getLogger(SkippedRidsService.class);

    private int lookBackUntilRid;
    private String keySkippedRids;
    private boolean skippedRidsFound = true;

    private GenerateUserIdManager generateUserIdManager;
    private UserAccountManager userAccountManager;
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public SkippedRidsService(
            /**
             * This limit is based on number of signup between server restarts.
             * In future this number would have to be adjusted.
             */
            @Value ("${lookBackUntilRid:500}")
            int lookBackUntilRid,

            @Value ("${redis.key.skippedRids}")
            String keySkippedRids,

            GenerateUserIdManager generateUserIdManager,
            UserAccountManager userAccountManager,
            RedisTemplate<String, Object> redisTemplate) {
        this.lookBackUntilRid = lookBackUntilRid;
        this.keySkippedRids = keySkippedRids;

        this.generateUserIdManager = generateUserIdManager;
        this.userAccountManager = userAccountManager;
        this.redisTemplate = redisTemplate;

        /**
         * Code below Will override values in keySkippedRids if any server is installed using this code.
         * To prevent override, add condition to check if keySkippedRids exists in REDIS.
         *
         * For now that condition does not matter.
         */
        ConcurrentLinkedQueue<Long> dequeRids = new ConcurrentLinkedQueue<>(findSkippedRids());
        if (dequeRids.isEmpty()) {
            skippedRidsFound = false;
        } else {
            this.redisTemplate.expire(keySkippedRids, 1, TimeUnit.DAYS);
            this.redisTemplate.opsForValue().set(keySkippedRids, dequeRids);
            LOG.warn("Size of skipped rids={} ids={}", dequeRids.size(), dequeRids);
        }
    }

    boolean hasSkippedRidsFound() {
        LOG.info("skippedRidsFound={} redisTemplate.hasKey={}", skippedRidsFound, redisTemplate.hasKey(keySkippedRids));
        return skippedRidsFound && redisTemplate.hasKey(keySkippedRids);
    }

    String getNextAutoGeneratedUserId() {
        ConcurrentLinkedQueue<Long> skippedRids = getSkippedRids();
        LOG.info("Fetched from Redis skippedRids={}", skippedRids);
        Assert.notEmpty(skippedRids);

        String rid = String.valueOf(skippedRids.poll());
        if (skippedRids.isEmpty()) {
            /* Marking false ensures another request does not fetches skipped RID's from Redis */
            skippedRidsFound = false;
            redisTemplate.delete(keySkippedRids);
        } else {
            LOG.info("Writing to Redis updated skippedRids={}", skippedRids);
            redisTemplate.opsForValue().set(keySkippedRids, skippedRids);
        }
        return rid;
    }

    private ConcurrentLinkedQueue<Long> getSkippedRids() {
        return (ConcurrentLinkedQueue<Long>) redisTemplate.opsForValue().get(keySkippedRids);
    }

    /**
     * Find all the RID's that have been skipped because of some issue. This can happen.
     * Note: The code is not adaptive. Should find skipped RID's. But this code finds it
     * when server re-starts.
     *
     * @return
     */
    private Set<Long> findSkippedRids() {
        long lastGenerateUserId = generateUserIdManager.getLastGenerateUserId();

        /* On Dec 4th 2016, Added +1 as changed to "open" group to exclude the last number by including in the list. */
        List<UserAccountEntity> userAccounts = userAccountManager.getLastSoManyRecords(lookBackUntilRid + 1);
        Set<Long> userAccountWithIds = new HashSet<>();
        for (UserAccountEntity userAccount : userAccounts) {
            userAccountWithIds.add(Long.parseLong(userAccount.getReceiptUserId()));
        }

        Set<Long> skippedRids = new HashSet<>();
        long start = lastGenerateUserId - lookBackUntilRid;
        if (start < GenerateUserIds.STARTING_USER_ID) {
            start = GenerateUserIds.STARTING_USER_ID;
        }

        while (start < lastGenerateUserId) {
            /**
             * Increase the count 'start' before adding as lookBackUntilRid starts with a number higher than start.
             * Semi-open group excludes starting number and includes the end number. Hence (1,4], i.e 2,3,4
             * and not 1,2,3,4.
             *
             * On Dec 4th 2016, changed to "open" group to exclude the last number.
             *
             * [a,b] is a "closed" group, meaning that a and b are indeed included within the group.
             * For example: the natural group [1,4] consists of 1,2,3,4.
             *
             * (a,b) is an "open" group, meaning that a and b are not a part of the group.
             * For example, the natural group (1,4) consists of 2,3.
             *
             * Groups can also be "semi-open" (or -closed) on one side, such as the natural group [1,4),
             * which would consist of 1,2,3; or (1,4], consisting of 2,3,4.
             */
            start++;
            skippedRids.add(start);
        }
        skippedRids.removeAll(userAccountWithIds);
        return skippedRids;
    }
}