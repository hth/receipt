/**
 *
 */
package com.tholix.web;

import java.math.BigDecimal;
import java.util.List;

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

import com.tholix.domain.ExpenseTypeEntity;
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
            itemAnalyticForm.setItem(item);
            itemAnalyticForm.setDays(NINETY_DAYS);

            DateTime untilThisDay = DateTime.now().minusDays(NINETY_DAYS);

            /** Gets site average */
            List<ItemEntity> siteAverageItems = itemAnalyticService.findAllByNameLimitByDays(item.getName(), untilThisDay);
            itemAnalyticForm.setSiteAverageItems(siteAverageItems);

            BigDecimal siteAveragePrice = itemAnalyticService.calculateAveragePrice(siteAverageItems);
            itemAnalyticForm.setSiteAveragePrice(siteAveragePrice);

            /** Users historical items */
            List<ItemEntity> yourItems = itemAnalyticService.findAllByName(item, userSession.getUserProfileId());
            itemAnalyticForm.setYourHistoricalItems(yourItems);

            /** Your average */
            List<ItemEntity> yourAverageItems = itemAnalyticService.findAllByNameLimitByDays(item.getName(), userSession.getUserProfileId(), untilThisDay);
            itemAnalyticForm.setYourAverageItems(yourAverageItems);

            BigDecimal yourAveragePrice = itemAnalyticService.calculateAveragePrice(yourAverageItems);
            itemAnalyticForm.setYourAveragePrice(yourAveragePrice);

            /** Loads expense types */
            List<ExpenseTypeEntity> expenseTypes = expensesService.activeExpenseTypes(item.getUserProfileId());
            itemAnalyticForm.setExpenseTypes(expenseTypes);
        }

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("itemAnalyticForm", itemAnalyticForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

}
