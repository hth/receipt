package com.receiptofi.domain.value;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ExpenseTagEntity;

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

    @Field ("TAX")
    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double tax;

    @Field ("TOT")
    @NumberFormat (style = NumberFormat.Style.CURRENCY)
    private Double total;

    @DBRef (lazy = false)
    @Field ("EXPENSE_TAG")
    private ExpenseTagEntity expenseTag;

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
}
