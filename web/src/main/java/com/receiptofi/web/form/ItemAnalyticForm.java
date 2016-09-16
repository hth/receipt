package com.receiptofi.web.form;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.utils.LocaleUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: hitender
 * Date: 5/27/13
 * Time: 12:59 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ItemAnalyticForm {

    private ItemEntity item;
    private BigDecimal yourAveragePrice;
    private BigDecimal siteAveragePrice;
    private List<ExpenseTagEntity> expenseTags;
    private List<ItemEntity> siteAverageItems;
    private List<ItemEntity> yourAverageItems;
    private List<ItemEntity> yourHistoricalItems;
    private int days;
    private String message;
    private long historicalCount;

    private ItemAnalyticForm() {
    }

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

    @SuppressWarnings("unused")
    public String getSiteAveragePriceString() {
        return LocaleUtil.getNumberFormat(item.getReceipt().getCountryShortName()).format(siteAveragePrice);
    }

    public void setSiteAveragePrice(BigDecimal siteAveragePrice) {
        this.siteAveragePrice = siteAveragePrice;
    }

    public BigDecimal getYourAveragePrice() {
        return yourAveragePrice;
    }

    @SuppressWarnings("unused")
    public String getYourAveragePriceString() {
        return LocaleUtil.getNumberFormat(item.getReceipt().getCountryShortName()).format(yourAveragePrice);
    }

    public void setYourAveragePrice(BigDecimal yourAveragePrice) {
        this.yourAveragePrice = yourAveragePrice;
    }

    public List<ExpenseTagEntity> getExpenseTags() {
        return expenseTags;
    }

    public void setExpenseTags(List<ExpenseTagEntity> expenseTags) {
        this.expenseTags = expenseTags;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getHistoricalCount() {
        return historicalCount;
    }

    public void setHistoricalCount(long historicalCount) {
        this.historicalCount = historicalCount;
    }
}
