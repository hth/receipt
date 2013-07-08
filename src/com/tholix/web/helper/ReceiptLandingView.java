package com.tholix.web.helper;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import com.tholix.domain.ReceiptEntity;

/**
 * User: hitender
 * Date: 7/6/13
 * Time: 12:54 PM
 */
public class ReceiptLandingView {

    String id;
    String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Date date;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    Double tax;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    Double total;

    String userProfileId;

    private ReceiptLandingView() {}

    public static ReceiptLandingView newInstance(ReceiptEntity receiptEntity) {
        ReceiptLandingView receiptLandingView = new ReceiptLandingView();
        receiptLandingView.setId(receiptEntity.getId());
        receiptLandingView.setName(receiptEntity.getBizName().getName());
        receiptLandingView.setDate(receiptEntity.getReceiptDate());
        receiptLandingView.setTax(receiptEntity.getTax());
        receiptLandingView.setTotal(receiptEntity.getTotal());
        receiptLandingView.setUserProfileId(receiptEntity.getUserProfileId());
        return receiptLandingView;
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
}
