package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.ItemEntity;
import com.tholix.repository.ItemManager;
import com.tholix.utils.Formatter;

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

    public Double calculateAveragePrice(String itemName) {
        List<ItemEntity> items = findAllByName(itemName);
        Double averagePrice = 0.00;
        for(ItemEntity item : items) {
            averagePrice = averagePrice + item.getPrice();
        }
        averagePrice = averagePrice/items.size();
        averagePrice = new Double(Formatter.df.format(averagePrice));
        return averagePrice;
    }

    private List<ItemEntity> findAllByName(String itemName) {
        return itemManager.getAllObjectWithName(itemName);
    }
}
