package com.receiptofi.web.controller.access;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.ItemService;
import com.receiptofi.web.form.ExpenseForm;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Lists out expenses related items. Call made from Pie chart on Tab 2
 * User: hitender
 * Date: 5/23/13
 * Time: 11:28 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/expenses")
public class ExpensesController {
    private static final Logger LOG = LoggerFactory.getLogger(ExpensesController.class);

    @Value ("${ExpensesController.nextPage:/expenses2}")
    private String nextPage;

    @Autowired private ItemService itemService;
    @Autowired private ExpensesService expensesService;

    @RequestMapping (value = "{tag}", method = RequestMethod.GET)
    public String forExpenseType(
            @PathVariable
            String tag,

            @ModelAttribute ("expenseForm")
            ExpenseForm expenseForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.debug("rid={} expenseType={}", receiptUser.getRid(), tag);
        List<ExpenseTagEntity> expenseTags = expensesService.getExpenseTags(receiptUser.getRid());
        List<ItemEntity> items = new ArrayList<>();

        if (!"Un-Assigned".equalsIgnoreCase(tag)) {
            for (ExpenseTagEntity expenseTagEntity : expenseTags) {
                if (expenseTagEntity.getTagName().equalsIgnoreCase(tag)) {
                    items = itemService.itemsForExpenseType(expenseTagEntity);
                    break;
                }
            }
        } else if ("Un-Assigned".equalsIgnoreCase(tag)) {
            items = itemService.itemsForUnAssignedExpenseType(receiptUser.getRid());
        }

        expenseForm.setName(tag);
        expenseForm.setExpenseTags(expenseTags);
        expenseForm.setItems(items);

        return nextPage;
    }
}
