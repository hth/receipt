package com.receiptofi.domain.value;

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

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private BigDecimal total;

    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private BigDecimal splitTotal;

    private List<ReceiptListViewGrouped> receiptListViewGroupedList;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getSplitTotal() {
        return splitTotal;
    }

    public void setSplitTotal(BigDecimal splitTotal) {
        this.splitTotal = splitTotal;
    }

    public List<ReceiptListViewGrouped> getReceiptListViewGroupedList() {
        return receiptListViewGroupedList;
    }

    public void setReceiptListViewGroupedList(List<ReceiptListViewGrouped> receiptListViewGroupedList) {
        this.receiptListViewGroupedList = receiptListViewGroupedList;
    }
}
