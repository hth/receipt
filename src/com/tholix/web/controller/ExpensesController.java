package com.tholix.web.controller;

import java.util.ArrayList;
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
import com.tholix.service.ItemService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ExpenseForm;

/**
 * Lists out expenses related items. Call made from Pie chart on Tab 2
 *
 * User: hitender
 * Date: 5/23/13
 * Time: 11:28 PM
 */
@Controller
@RequestMapping(value = "/expenses")
@SessionAttributes({"userSession"})
public class ExpensesController {
    private static final Logger log = Logger.getLogger(ExpensesController.class);
    private static final String nextPage = "/expenses";

    @Autowired private ItemService itemService;
    @Autowired private ExpensesService expensesService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView forExpenseType(@RequestParam("type") String expenseType, @ModelAttribute("expenseForm") ExpenseForm expenseForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();

        List<ExpenseTypeEntity> expenseTypes = expensesService.activeExpenseTypes(userSession.getUserProfileId());
        List<ItemEntity> items = new ArrayList<>();

        if(!expenseType.equalsIgnoreCase("Un-Assigned")) {
            for(ExpenseTypeEntity expenseTypeEntity : expenseTypes) {
                if(expenseTypeEntity.getExpName().equalsIgnoreCase(expenseType)) {
                    items = itemService.itemsForExpenseType(expenseTypeEntity);
                    break;
                }
            }
        } else if(expenseType.equalsIgnoreCase("Un-Assigned")) {
            items = itemService.itemsForUnAssignedExpenseType(userSession.getUserProfileId());
        }

        expenseForm.setName(expenseType);
        expenseForm.setExpenseTypes(expenseTypes);
        expenseForm.setItems(items);

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("expenseForm", expenseForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }
}
