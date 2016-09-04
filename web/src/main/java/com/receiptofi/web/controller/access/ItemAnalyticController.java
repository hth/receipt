/**
 *
 */
package com.receiptofi.web.controller.access;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.ItemAnalyticService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.ItemAnalyticForm;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author hitender
 * @since Jan 9, 2013 10:23:55 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/itemanalytic")
public class ItemAnalyticController {
    private static final Logger LOG = LoggerFactory.getLogger(ItemAnalyticController.class);

    @Value ("${ItemAnalyticController.nextPage:/itemanalytic}")
    private String nextPage;

    @Value ("${ItemAnalyticController.searchLimitForDays:180}")
    private int searchLimitForDays;

    @Value ("${ItemAnalyticController.itemLimit:20}")
    private int itemLimit;

    private final ItemAnalyticService itemAnalyticService;
    private final ExpensesService expensesService;

    @Autowired
    public ItemAnalyticController(ExpensesService expensesService, ItemAnalyticService itemAnalyticService) {
        this.expensesService = expensesService;
        this.itemAnalyticService = itemAnalyticService;
    }

    @RequestMapping (value = "{itemId}", method = RequestMethod.GET)
    public String loadForm(
            @PathVariable("itemId")
            ScrubbedInput itemId,

            @ModelAttribute ("itemAnalyticForm")
            ItemAnalyticForm itemAnalyticForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ItemEntity item = itemAnalyticService.findItemById(itemId.getText(), receiptUser.getRid());
        if (null != item) {
            itemAnalyticForm.setItem(item);
            itemAnalyticForm.setDays(searchLimitForDays);

            DateTime untilThisDay = DateTime.now().minusDays(searchLimitForDays);
            if (item.getReceipt().getReceiptDate().before(untilThisDay.toDate())) {
                itemAnalyticForm.setMessage("Item " +  item.getName() +
                                " was purchased more than " + searchLimitForDays +
                                " days ago, hence no average computed.");
            }

            /** Gets site average */
            List<ItemEntity> siteAverageItems = itemAnalyticService.findAllByNameLimitByDays(
                    item.getName(),
                    untilThisDay);
            itemAnalyticForm.setSiteAverageItems(siteAverageItems);

            BigDecimal siteAveragePrice = itemAnalyticService.calculateAveragePrice(siteAverageItems);
            itemAnalyticForm.setSiteAveragePrice(siteAveragePrice);

            /** Your average */
            List<ItemEntity> yourAverageItems = itemAnalyticService.findAllByNameLimitByDays(
                    item.getName(),
                    receiptUser.getRid(),
                    untilThisDay);
            itemAnalyticForm.setYourAverageItems(yourAverageItems);

            BigDecimal yourAveragePrice = itemAnalyticService.calculateAveragePrice(yourAverageItems);
            itemAnalyticForm.setYourAveragePrice(yourAveragePrice);

            /** Users historical items */
            itemAnalyticForm.setHistoricalCount(itemAnalyticService.findAllByNameCount(item, receiptUser.getRid()));
            List<ItemEntity> yourItems = itemAnalyticService.findAllByName(item, receiptUser.getRid(), itemLimit);
            itemAnalyticForm.setYourHistoricalItems(yourItems);

            /** Loads expense types */
            List<ExpenseTagEntity> expenseTags = expensesService.getExpenseTags(item.getReceiptUserId());
            itemAnalyticForm.setExpenseTags(expenseTags);
        }

        return nextPage;
    }
}
