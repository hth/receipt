package com.receiptofi.web.helper;

import com.receiptofi.domain.ReceiptEntity;

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
    private Double tax;

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double total;

    private String userProfileId;
    private String bizNameForId;
    private String expenseReportInFS;
    private String expenseTag;
    private String expenseColor;

    private ReceiptLandingView(ReceiptEntity receiptEntity) {
        id = receiptEntity.getId();
        name = receiptEntity.getBizName().getBusinessName();
        date = receiptEntity.getReceiptDate();
        tax = receiptEntity.getTax();
        total = receiptEntity.getTotal();
        userProfileId = receiptEntity.getReceiptUserId();
        expenseReportInFS = receiptEntity.getExpenseReportInFS();
        if (null != receiptEntity.getExpenseTag()) {
            expenseTag = receiptEntity.getExpenseTag().getTagName();
            expenseColor = receiptEntity.getExpenseTag().getTagColor();
        }

        /** Remove all alpha numeric characters as it creates issues with 'id' */
        bizNameForId = StringUtils.deleteWhitespace(receiptEntity.getBizName().getBusinessName()).replaceAll("[^a-zA-Z0-9]", "");
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

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
}
