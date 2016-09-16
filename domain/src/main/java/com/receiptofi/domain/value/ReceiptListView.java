package com.receiptofi.domain.value;

import com.receiptofi.utils.LocaleUtil;
import com.receiptofi.utils.Maths;

import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2/6/15 3:08 PM
 */
public class ReceiptListView {

    private int year;
    private int month;
    private Date date;
    private String countryShortName;

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private BigDecimal splitTotal;

    private List<ReceiptListViewGrouped> receiptListViewGroupedList;

    public int getYear() {
        return year;
    }

    public ReceiptListView setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public ReceiptListView setMonth(int month) {
        this.month = month;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ReceiptListView setDate(Date date) {
        this.date = date;
        return this;
    }

    public BigDecimal getSplitTotal() {
        return splitTotal;
    }

    @SuppressWarnings("unused")
    public String getSplitTotalString() {
        return LocaleUtil.getNumberFormat(countryShortName).format(Maths.adjustScale(splitTotal));
    }

    public ReceiptListView setSplitTotal(BigDecimal splitTotal) {
        this.splitTotal = splitTotal;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public ReceiptListView setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public List<ReceiptListViewGrouped> getReceiptListViewGroupedList() {
        return receiptListViewGroupedList;
    }

    public void setReceiptListViewGroupedList(List<ReceiptListViewGrouped> receiptListViewGroupedList) {
        this.receiptListViewGroupedList = receiptListViewGroupedList;
    }
}
