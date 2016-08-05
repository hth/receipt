package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Uni directional exhibition of RID expenses with AID.
 * User: hitender
 * Date: 7/23/16 8:35 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "ACCOUNTANT")
@CompoundIndexes (value = {
        @CompoundIndex (
                name = "accountant_rid_aid_idx",
                def = "{'RID': 1, 'AID': 1}",
                background = true,
                unique = true)
})
public class AccountantEntity extends BaseEntity {

    /* Initiator. */
    @NotNull
    @Field ("RID")
    private String receiptUserId;

    /* Account user has to be with Accountant Role. */
    @NotNull
    @Field ("AID")
    private String accountantUserId;

    /* Set to true on acceptance by friend user. */
    @Field ("AC")
    private boolean acceptConnection;

    @Field ("CON")
    private boolean connected;

    /* Id of the person who initiates unfriend or disconnects. */
    @Field ("DI")
    private String disconnectInitiator;

    /* Secret key which is required to access receipt marked with expense tag. */
    @Field ("AUTH")
    private String authenticationKey;

    /* List of shared expense tags. */
    @Field ("ET")
    private List<String> expenseTags;

    /* Delay sharing receipt by number of days. For privacy sake. */
    @Field ("DD")
    private int delayDuration;

    /* Day when connection was accepted. */
    @Field ("CS")
    private Date connectSince;

    @Field ("AH")
    private List<AccessHistory> accessHistories;

    private AccountantEntity(String receiptUserId, String accountantUserId, String authenticationKey) {
        this.receiptUserId = receiptUserId;
        this.accountantUserId = accountantUserId;
        this.authenticationKey = authenticationKey;
    }

    public static AccountantEntity newInstance(String receiptUserId, String tallyUserId, String authenticationKey) {
        return new AccountantEntity(receiptUserId, tallyUserId, authenticationKey);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getAccountantUserId() {
        return accountantUserId;
    }

    public boolean isAcceptConnection() {
        return acceptConnection;
    }

    public void setAcceptConnection(boolean acceptConnection) {
        this.acceptConnection = acceptConnection;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getDisconnectInitiator() {
        return disconnectInitiator;
    }

    public void setDisconnectInitiator(String disconnectInitiator) {
        this.disconnectInitiator = disconnectInitiator;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public List<String> getExpenseTags() {
        return expenseTags;
    }

    public void setExpenseTags(List<String> expenseTags) {
        this.expenseTags = expenseTags;
    }

    public int getDelayDuration() {
        return delayDuration;
    }

    public void setDelayDuration(int delayDuration) {
        this.delayDuration = delayDuration;
    }

    public Date getConnectSince() {
        return connectSince;
    }

    public void setConnectSince(Date connectSince) {
        this.connectSince = connectSince;
    }

    public List<AccessHistory> getAccessHistories() {
        return accessHistories;
    }

    public void setAccessHistories(List<AccessHistory> accessHistories) {
        this.accessHistories = accessHistories;
    }

    @Override
    public String toString() {
        return "ExhibitExpenseEntity{" +
                "receiptUserId='" + receiptUserId + '\'' +
                ", accountantUserId='" + accountantUserId + '\'' +
                ", acceptConnection=" + acceptConnection +
                ", connected=" + connected +
                ", disconnectInitiator='" + disconnectInitiator + '\'' +
                ", delayDuration=" + delayDuration +
                ", connectSince=" + connectSince +
                '}';
    }
}
