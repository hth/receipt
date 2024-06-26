package com.receiptofi.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

import com.receiptofi.domain.ItemEntity;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.utils.Maths;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 7:38 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ItemAnalyticService {
    private static final Logger LOG = LoggerFactory.getLogger(ItemAnalyticService.class);

    private static Ordering<ItemEntity> ascendingOrderForItems = new Ordering<ItemEntity>() {
        public int compare(ItemEntity left, ItemEntity right) {
            return Longs.compare(left.getReceipt().getReceiptDate().getTime(), right.getReceipt().getReceiptDate().getTime());
        }
    };

    private static Ordering<ItemEntity> descendingOrderForItems = new Ordering<ItemEntity>() {
        public int compare(ItemEntity left, ItemEntity right) {
            return Longs.compare(right.getReceipt().getReceiptDate().getTime(), left.getReceipt().getReceiptDate().getTime());
        }
    };

    @Autowired private ItemManager itemManager;

    public ItemEntity findItemById(String itemId, String receiptUserId) {
        return itemManager.findItem(itemId, receiptUserId);
    }

    /**
     * Calculates average price paid for the similar item by others
     *
     * @param items
     * @return
     */
    public BigDecimal calculateAveragePrice(Iterable<ItemEntity> items) {
        int count = 0;
        BigDecimal averagePrice = BigDecimal.ZERO;
        for (ItemEntity item : items) {
            averagePrice = Maths.add(averagePrice, item.getPrice());
            count++;
        }
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        averagePrice = Maths.divide(averagePrice, count);
        return averagePrice;
    }

    /**
     * Get all the items with similar name after the specified date.
     * Example: If Item date is March 31 then find all the items with same name after Jan 1. The range is between
     * Jan 1 and March 31
     *
     * @param itemName
     * @param untilThisDay - limiting factor for selecting items
     * @return List<ItemEntity> - Sorted ascending order
     */
    public List<ItemEntity> findAllByNameLimitByDays(String itemName, final DateTime untilThisDay) {
        List<ItemEntity> items = itemManager.findAllByNameLimitByDays(itemName, untilThisDay);
        List<ItemEntity> filteredItems = filterItemsByUntilThisDay(untilThisDay, items);
        return ascendingOrderForItems.sortedCopy(filteredItems);
    }

    /**
     * Get all the items with similar name after the specified date.
     * Example: If Item date is March 31 then find all the items with same name after Jan 1. The range is between
     * Jan 1 and March 31
     *
     * @param itemName
     * @param untilThisDay - limiting factor for selecting items
     * @return List<ItemEntity> - Sorted ascending order
     */
    public List<ItemEntity> findAllByNameLimitByDays(String itemName, String userProfileId, final DateTime untilThisDay) {
        List<ItemEntity> items = itemManager.findAllByNameLimitByDays(itemName, userProfileId, untilThisDay);
        List<ItemEntity> filteredItems = filterItemsByUntilThisDay(untilThisDay, items);
        return ascendingOrderForItems.sortedCopy(filteredItems);
    }

    /**
     * Limit list content until this day
     *
     * @param untilThisDay
     * @param items
     * @return
     */
    private List<ItemEntity> filterItemsByUntilThisDay(final DateTime untilThisDay, List<ItemEntity> items) {
        Iterable<ItemEntity> filtered = Iterables.filter(items, new Predicate<ItemEntity>() {
            @Override
            public boolean apply(ItemEntity item) {
                return item.getReceipt().getReceiptDate().after(untilThisDay.toDate());
            }
        });

        return Lists.newArrayList(filtered.iterator());
    }

    /**
     * Get all matching items and then sort descending based on receipt date and limit up to 10 (0,10)
     * Note: Providing a user profile id is redundant but its critical to make sure only the user of
     * that session is requesting its own list of items. Otherwise there could be privacy issues.
     *
     * @param item
     * @param userProfileId
     * @return
     */
    public List<ItemEntity> findAllByName(ItemEntity item, String userProfileId, int itemLimit) {
        List<ItemEntity> items = itemManager.findAllByName(item, userProfileId, itemLimit);
        return descendingOrderForItems.sortedCopy(items);
    }

    public long findAllByNameCount(ItemEntity item, String userProfileId) {
        return itemManager.findAllByNameCount(item, userProfileId);
    }

    @SuppressWarnings ("unused")
    private List<ItemEntity> callDescendingOrdering(List<ItemEntity> items) {
        return descendingOrderForItems.sortedCopy(items);
    }
}
