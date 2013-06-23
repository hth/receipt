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
    private BigDecimal yourAveragePrice;
    private BigDecimal siteAveragePrice;
    private List<ExpenseTypeEntity> expenseTypes;
    private List<ItemEntity> siteAverageItems;
    private List<ItemEntity> yourAverageItems;
    private List<ItemEntity> yourHistoricalItems;
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

    public BigDecimal getSiteAveragePrice() {
        return siteAveragePrice;
    }

    public void setSiteAveragePrice(BigDecimal siteAveragePrice) {
        this.siteAveragePrice = siteAveragePrice;
    }

    public BigDecimal getYourAveragePrice() {
        return yourAveragePrice;
    }

    public void setYourAveragePrice(BigDecimal yourAveragePrice) {
        this.yourAveragePrice = yourAveragePrice;
    }

    public List<ExpenseTypeEntity> getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(List<ExpenseTypeEntity> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }

    public Iterable<ItemEntity> getSiteAverageItems() {
        return siteAverageItems;
    }

    /**
     * Site average
     *
     * @param siteAverageItems
     */
    public void setSiteAverageItems(List<ItemEntity> siteAverageItems) {
        this.siteAverageItems = siteAverageItems;
    }

    public Iterable<ItemEntity> getYourAverageItems() {
        return yourAverageItems;
    }

    /**
     * Your average
     *
     * @param yourAverageItems
     */
    public void setYourAverageItems(List<ItemEntity> yourAverageItems) {
        this.yourAverageItems = yourAverageItems;
    }

    public List<ItemEntity> getYourHistoricalItems() {
        return yourHistoricalItems;
    }

    /**
     * Users historical purchases
     *
     * @param yourHistoricalItems
     */
    public void setYourHistoricalItems(List<ItemEntity> yourHistoricalItems) {
        this.yourHistoricalItems = yourHistoricalItems;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
