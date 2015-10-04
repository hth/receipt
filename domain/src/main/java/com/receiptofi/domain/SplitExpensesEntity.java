package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import junit.framework.Assert;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 9/20/15 1:30 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "SPLIT_EXPENSES")
@CompoundIndexes (value = {
        @CompoundIndex (
                name = "split_expenses_rid_fid_idx",
                def = "{'RDID': -1, 'RID': 1, 'FID': 1}",
                background = true,
                unique = true)
})
public class SplitExpensesEntity extends BaseEntity {

    @NotNull
    @Field ("RDID")
    private String receiptDocumentId;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("FID")
    private String friendUserId;

    @NotNull
    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    @Field ("ST")
    private Double splitTotal;

    /** To keep bean happy. */
    @SuppressWarnings ("unused")
    private SplitExpensesEntity() {
        super();
    }

    public SplitExpensesEntity(String receiptDocumentId, String receiptUserId, String fid) {
        this.receiptDocumentId = receiptDocumentId;
        this.receiptUserId = receiptUserId;
        /**
         * There is always a possibility that a friend could be same as receipt user id in receipt.
         * So select the other friend.
         */
        this.friendUserId = fid;
    }

    public String getReceiptDocumentId() {
        return receiptDocumentId;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public Double getSplitTotal() {
        return splitTotal;
    }
}
