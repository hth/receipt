/**
 *
 */
package com.tholix.web;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ExpensesService;
import com.tholix.service.ItemAnalyticService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ItemAnalyticForm;

/**
 * @author hitender
 * @since Jan 9, 2013 10:23:55 PM
 *
 */
@Controller
@RequestMapping(value = "/itemanalytic")
@SessionAttributes({"userSession"})
public class ItemAnalyticController {
	private static final Logger log = Logger.getLogger(ItemAnalyticController.class);
	private static final String nextPage = "/itemanalytic";

    private static final int NINETY_DAYS = 90;

	@Autowired private ItemAnalyticService itemAnalyticService;
    @Autowired private ExpensesService expensesService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id, @ModelAttribute("itemAnalyticForm") ItemAnalyticForm itemAnalyticForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();

		ItemEntity item = itemAnalyticService.findItemById(id, userSession.getUserProfileId());
        if(item != null) {
            DateTime untilThisDay = DateTime.now().minusDays(NINETY_DAYS);
            Iterable<ItemEntity> items = itemAnalyticService.findAllByNameLimitByDays(item.getName(), untilThisDay);
            BigDecimal averagePrice = itemAnalyticService.calculateAveragePrice(items);

            itemAnalyticForm.setDays(NINETY_DAYS);
            itemAnalyticForm.setItem(item);
            itemAnalyticForm.setAveragePrice(averagePrice);
            itemAnalyticForm.setExpenseTypes(expensesService.activeExpenseTypes(item.getUserProfileId()));

            items = itemAnalyticService.findAllByName(item, userSession.getUserProfileId());
            itemAnalyticForm.setItems(items);
        }

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("itemAnalyticForm", itemAnalyticForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

}
