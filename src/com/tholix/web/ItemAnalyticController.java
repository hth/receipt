/**
 * 
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.service.ItemManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;
import com.tholix.utils.PerformanceProfiling;

/**
 * @author hitender 
 * @when Jan 9, 2013 10:23:55 PM
 *
 */
@Controller
@RequestMapping(value = "/itemanalytic")
public class ItemAnalyticController {
	private static final Logger log = Logger.getLogger(ItemAnalyticController.class);
	private static final String nextPage = "/itemanalytic";

	@Autowired private ItemManager itemManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id) {
        DateTime time = DateUtil.now();
		ItemEntity myItem = itemManager.findOne(id);
		List<ItemEntity> items = itemManager.getAllObjectWithName(myItem.getName());		
		
		Double averagePrice = 0.00;
		for(ItemEntity item : items) {
			averagePrice = averagePrice + item.getPrice();
		}		
		averagePrice = averagePrice/items.size();
		averagePrice = new Double(Formatter.df.format(averagePrice));
		
		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("item", myItem);
		modelAndView.addObject("averagePrice", averagePrice);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}
	
	
}
