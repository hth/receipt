package com.tholix.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.repository.ItemManager;
import com.tholix.utils.Maths;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 7:38 PM
 */
@Service
public class ItemAnalyticService {

    @Autowired private ItemManager itemManager;

    public ItemEntity findItemById(String itemId) {
        return itemManager.findOne(itemId);
    }

    /**
     * Calculates average price paid for the similar item by others
     *
     * @param items
     * @return
     */
    public BigDecimal calculateAveragePrice(List<ItemEntity> items) {
        BigDecimal averagePrice = BigDecimal.ZERO;
        for(ItemEntity item : items) {
            averagePrice = Maths.add(averagePrice, item.getPrice());
        }
        averagePrice = Maths.divide(averagePrice, items.size());
        return averagePrice;
    }

    /**
     * Get all the items with similar name
     *
     * @param itemName
     * @return
     */
    public List<ItemEntity> findAllByNameLimitByDays(String itemName, DateTime untilThisDay) {
        return itemManager.findAllByNameLimitByDays(itemName, untilThisDay);
    }

    /**
     * Get all the historical items for a user.
     *
     * Note: Providing a user profile id is redundant but its critical to make sure only the user of
     * that session is requesting its own list of items. Otherwise there could be privacy issues.
     *
     * @param item
     * @param userProfileId
     * @return
     */
    public List<ItemEntity> findAllByName(ItemEntity item, String userProfileId) {
        return itemManager.findAllByName(item, userProfileId);
    }
}
