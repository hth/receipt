package com.receiptofi.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.receiptofi.domain.ExpenseTypeEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.ItemService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.ExpenseForm;

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

    @RequestMapping(value = "{expenseType}", method = RequestMethod.GET)
    public ModelAndView forExpenseType(@PathVariable String expenseType, @ModelAttribute("expenseForm") ExpenseForm expenseForm, @ModelAttribute("userSession") UserSession userSession) {
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
