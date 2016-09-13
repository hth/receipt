package com.receiptofi.domain.value;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.utils.LocaleUtil;
import com.receiptofi.utils.Maths;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;

/**
 * User: hitender
 * Date: 2/6/15 4:20 AM
 */
public class ReceiptListViewGrouped {
    private String id;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @Field ("RTXD")
    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    @Field ("SX")
    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double splitTax;

    @Field ("ST")
    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double splitTotal;

    /** Defaults being shared with self hence 1. */
    @Field ("SC")
    private int splitCount = 1;

    @DBRef (lazy = false)
    @Field ("EXPENSE_TAG")
    private ExpenseTagEntity expenseTag;

    @Field ("BS")
    private BilledStatusEnum billedStatus = BilledStatusEnum.NB;

    @Field ("RF")
    private String referToReceiptId;

    @Field ("CS")
    private String countryShortName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return bizName.getBusinessName();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getSplitTax() {
        return splitTax;
    }

    public void setSplitTax(Double splitTax) {
        this.splitTax = splitTax;
    }

    public Double getSplitTotal() {
        return splitTotal;
    }

    public String getSplitTotalString() {
        return LocaleUtil.getNumberFormat(countryShortName).format(Maths.adjustScale(splitTotal));
    }

    public void setSplitTotal(Double splitTotal) {
        this.splitTotal = splitTotal;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public ExpenseTagEntity getExpenseTag() {
        return expenseTag;
    }

    public void setExpenseTag(ExpenseTagEntity expenseTag) {
        this.expenseTag = expenseTag;
    }

    public String getExpenseTagName() {
        return expenseTag == null ? "" : expenseTag.getTagName();
    }

    public String getExpenseColor() {
        return expenseTag == null ? "" : expenseTag.getTagColor();
    }

    public BilledStatusEnum getBilledStatus() {
        return billedStatus;
    }

    public void setBilledStatus(BilledStatusEnum billedStatus) {
        this.billedStatus = billedStatus;
    }

    public boolean isOwnReceipt() {
        return StringUtils.isBlank(referToReceiptId);
    }
}
