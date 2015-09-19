package com.receiptofi.social.config;

import com.receiptofi.social.annotation.Social;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * User: hitender
 * Date: 7/9/14 12:27 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@Social
public class ProviderConfig {

    /**
     * Facebook friends are found when the friend is using ReceiptApp. Otherwise would not show up in friend list.
     */
    @Value ("${populate.social.friend.on:true}")
    private boolean populateSocialFriendOn;

    public boolean isPopulateSocialFriendOn() {
        return populateSocialFriendOn;
    }
}
