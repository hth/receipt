package com.receiptofi.web.helper;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.utils.LocaleUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;

/**
 * User: hitender
 * Date: 7/6/13
 * Time: 12:54 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ReceiptLandingView {

    private String id;
    private String name;

    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double splitTax;

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double splitTotal;
    private int splitCount;

    private String userProfileId;
    private String bizNameForId;
    private String expenseReportInFS;
    private String expenseTag;
    private String expenseColor;
    private BilledStatusEnum billedStatus;
    private boolean ownReceipt;
    private String countryShortName;

    private ReceiptLandingView(ReceiptEntity receipt) {
        id = receipt.getId();
        name = receipt.getBizName().getBusinessName();
        date = receipt.getReceiptDate();
        splitCount = receipt.getSplitCount();
        splitTax = receipt.getSplitTax();
        splitTotal = receipt.getSplitTotal();
        userProfileId = receipt.getReceiptUserId();
        expenseReportInFS = receipt.getExpenseReportInFS();
        if (null != receipt.getExpenseTag()) {
            expenseTag = receipt.getExpenseTag().getTagName();
            expenseColor = receipt.getExpenseTag().getTagColor();
        }

        /** Remove all alpha numeric characters as it creates issues with 'id' */
        bizNameForId = StringUtils.deleteWhitespace(receipt.getBizName().getBusinessName()).replaceAll("[^a-zA-Z0-9]", "");
        billedStatus = receipt.getBilledStatus();
        ownReceipt = StringUtils.isBlank(receipt.getReferReceiptId());
        countryShortName = receipt.getBizStore().getCountryShortName();
    }

    public static ReceiptLandingView newInstance(ReceiptEntity receiptEntity) {
        return new ReceiptLandingView(receiptEntity);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
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
        return LocaleUtil.getNumberFormat(countryShortName).format(splitTotal);
    }

    public void setSplitTotal(Double splitTotal) {
        this.splitTotal = splitTotal;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getBizNameForId() {
        return bizNameForId;
    }

    public void setBizNameForId(String bizNameForId) {
        this.bizNameForId = bizNameForId;
    }

    public String getExpenseReportInFS() {
        return expenseReportInFS;
    }

    public void setExpenseReportInFS(String expenseReportInFS) {
        this.expenseReportInFS = expenseReportInFS;
    }

    public String getExpenseTag() {
        return expenseTag;
    }

    public void setExpenseTag(String expenseTag) {
        this.expenseTag = expenseTag;
    }

    public String getExpenseColor() {
        return expenseColor;
    }

    public void setExpenseColor(String expenseColor) {
        this.expenseColor = expenseColor;
    }

    public BilledStatusEnum getBilledStatus() {
        return billedStatus;
    }

    public boolean isOwnReceipt() {
        return ownReceipt;
    }
}
