package com.tholix.web.form;

import java.math.BigDecimal;
import java.util.List;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;

/**
 * User: hitender
 * Date: 5/27/13
 * Time: 12:59 AM
 */
public final class ItemAnalyticForm {

    private ItemEntity item;
    private BigDecimal averagePrice;
    private List<ExpenseTypeEntity> expenseTypes;
    private List<ItemEntity> items;
    private int days;

    private ItemAnalyticForm() {}

    public static ItemAnalyticForm newInstance() {
        return new ItemAnalyticForm();
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public List<ExpenseTypeEntity> getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(List<ExpenseTypeEntity> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
