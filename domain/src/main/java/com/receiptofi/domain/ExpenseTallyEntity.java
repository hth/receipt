package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Uni directional exhibition of RID expenses with EID.
 * User: hitender
 * Date: 7/23/16 8:35 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "EXPENSE_TALLY")
@CompoundIndexes (value = {
        @CompoundIndex (
                name = "expense_tally_rid_tid_idx",
                def = "{'RID': 1, 'TID': 1}",
                background = true,
                unique = true)
})
public class ExpenseTallyEntity extends BaseEntity {

    /* Initiator. */
    @NotNull
    @Field ("RID")
    private String receiptUserId;

    /* Tally user has to be Business User. */
    @NotNull
    @Field ("TID")
    private String tallyUserId;

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

    /* Delay sharing receipt by number of days. */
    @Field ("DE")
    private int delayExhibit;

    /* Day when connection was accepted. */
    @Field ("CS")
    private Date connectSince;

    private ExpenseTallyEntity(String receiptUserId, String tallyUserId, String authenticationKey) {
        this.receiptUserId = receiptUserId;
        this.tallyUserId = tallyUserId;
        this.authenticationKey = authenticationKey;
    }

    public static ExpenseTallyEntity newInstance(String receiptUserId, String tallyUserId, String authenticationKey) {
        return new ExpenseTallyEntity(receiptUserId, tallyUserId, authenticationKey);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getTallyUserId() {
        return tallyUserId;
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

    public int getDelayExhibit() {
        return delayExhibit;
    }

    public void setDelayExhibit(int delayExhibit) {
        this.delayExhibit = delayExhibit;
    }

    public Date getConnectSince() {
        return connectSince;
    }

    public void setConnectSince(Date connectSince) {
        this.connectSince = connectSince;
    }

    @Override
    public String toString() {
        return "ExhibitExpenseEntity{" +
                "receiptUserId='" + receiptUserId + '\'' +
                ", tallyUserId='" + tallyUserId + '\'' +
                ", acceptConnection=" + acceptConnection +
                ", connected=" + connected +
                ", disconnectInitiator='" + disconnectInitiator + '\'' +
                ", delayExhibit=" + delayExhibit +
                ", connectSince=" + connectSince +
                '}';
    }
}
