package com.tholix.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param itemName
     * @return
     */
    public BigDecimal calculateAveragePrice(String itemName) {
        List<ItemEntity> items = findAllByName(itemName);
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
    public List<ItemEntity> findAllByName(String itemName) {
        return itemManager.getAllObjectWithName(itemName);
    }
}
