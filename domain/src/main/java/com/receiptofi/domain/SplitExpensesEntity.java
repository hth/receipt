package com.receiptofi.domain;

import com.receiptofi.domain.types.SplitStatusEnum;
import com.receiptofi.utils.LocaleUtil;
import com.receiptofi.utils.Maths;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

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

    @NotNull
    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE_TIME)
    @Field ("RTXD")
    private Date receiptDate;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field ("BIZ_STORE")
    private BizStoreEntity bizStore;

    @NotNull
    @Field ("SS")
    private SplitStatusEnum splitStatus = SplitStatusEnum.U;

    @NotNull
    @Field ("CS")
    private String countryShortName;

    /** To keep bean happy. */
    @SuppressWarnings ("unused")
    private SplitExpensesEntity() {
        super();
    }

    public SplitExpensesEntity(String fid, ReceiptEntity receipt) {

        this.receiptDocumentId = receipt.getId();
        this.receiptUserId = receipt.getReceiptUserId();
        /**
         * There is always a possibility that a friend could be same as receipt user id in receipt.
         * So select the other friend.
         */
        this.friendUserId = fid;
        this.receiptDate = receipt.getReceiptDate();

        this.bizName = receipt.getBizName();
        this.bizStore = receipt.getBizStore();
        this.countryShortName = receipt.getCountryShortName();
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

    @SuppressWarnings ("unused")
    @Transient
    public String getSplitTotalString() {
        return LocaleUtil.getNumberFormat(countryShortName).format(Maths.adjustScale(splitTotal));
    }

    public void setSplitTotal(Double splitTotal) {
        this.splitTotal = splitTotal;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public SplitStatusEnum getSplitStatus() {
        return splitStatus;
    }

    public void setSplitStatus(SplitStatusEnum splitStatus) {
        this.splitStatus = splitStatus;
    }

    public String getCountryShortName() {
        return countryShortName;
    }
}
