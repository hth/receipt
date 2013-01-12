/**
 * 
 */
package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ItemManager;
import com.tholix.utils.Formatter;

/**
 * @author hitender 
 * @when Jan 9, 2013 10:23:55 PM
 *
 */
@Controller
@RequestMapping(value = "/itemanalytic")
public class ItemAnalyticController {
	
	private String nextPage = "/itemanalytic";

	@Autowired ItemManager itemManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id) {
		ItemEntity myItem = itemManager.getObject(id);
		List<ItemEntity> items = itemManager.getAllObjectWithName(myItem.getName());		
		
		Double averagePrice = 0.00;
		for(ItemEntity item : items) {
			averagePrice = averagePrice + item.getPrice();
		}
		
		//TODO this is a redundant logic as there is always going to be one item in the list
		if(items != null && items.size() > 0) {
			averagePrice = averagePrice/items.size();
			averagePrice = new Double(Formatter.df.format(averagePrice));
		} else {
			averagePrice = myItem.getPrice();
		}		
		
		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("item", myItem);
		modelAndView.addObject("averagePrice", averagePrice);
		
		return modelAndView;
	}
}
