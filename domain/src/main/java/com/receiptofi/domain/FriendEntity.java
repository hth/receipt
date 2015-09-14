package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 9/13/15 9:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "FRIEND")
@CompoundIndexes (value = {
        @CompoundIndex (name = "friend_idx", def = "{'RID': 0, 'FID' : 0}", unique = true)
})
public class FriendEntity  extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("FID")
    private String friendUserId;

    /** To make bean happy. */
    public FriendEntity() {
        super();
    }

    public FriendEntity(String receiptUserId, String friendUserId) {
        this.receiptUserId = receiptUserId;
        this.friendUserId = friendUserId;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getFriendUserId() {
        return friendUserId;
    }
}
