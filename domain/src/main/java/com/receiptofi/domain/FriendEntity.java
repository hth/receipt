package com.receiptofi.domain;

import com.receiptofi.utils.RandomString;

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
        @CompoundIndex (name = "friend_idx", def = "{'RID': -1, 'FID' : -1}", unique = true)
})
public class FriendEntity  extends BaseEntity {

    /** Initiator. */
    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("FID")
    private String friendUserId;

    /** Set to true on acceptance by friend user. */
    @Field ("AC")
    private boolean acceptConnection;

    @Field ("CON")
    private boolean connected;

    /** Id of the person who initiates unfriend. */
    @Field ("UNF")
    private String unfriendUser;

    @Field ("AUTH")
    private String authenticationKey;

    /** To make bean happy. */
    public FriendEntity() {
        super();
    }

    public FriendEntity(String receiptUserId, String friendUserId) {
        this.receiptUserId = receiptUserId;
        this.friendUserId = friendUserId;
        this.authenticationKey = RandomString.newInstance().nextString();
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public boolean isAcceptConnection() {
        return acceptConnection;
    }

    public void acceptConnection() {
        this.acceptConnection = true;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect() {
        this.connected = true;
    }

    public void disconnect() {
        this.connected = false;
    }

    public String getUnfriendUser() {
        return unfriendUser;
    }

    public void setUnfriendUser(String unfriendUser) {
        this.unfriendUser = unfriendUser;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }
}
