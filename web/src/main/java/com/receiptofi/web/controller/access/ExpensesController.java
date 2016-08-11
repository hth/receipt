package com.receiptofi.web.controller.access;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.MailService;
import com.receiptofi.utils.ScrubbedInput;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Value ("${ExpensesController.nextPage:/expenses}")
    private String nextPage;

    private ItemService itemService;
    private ExpensesService expensesService;

    @Autowired
    public ExpensesController(
            ItemService itemService,
            ExpensesService expensesService) {
        this.itemService = itemService;
        this.expensesService = expensesService;
    }

    @RequestMapping (value = "/{tag}", method = RequestMethod.GET)
    public String forExpenseType(
            @PathVariable
            ScrubbedInput tag,

            @ModelAttribute ("expenseForm")
            ExpenseForm expenseForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String tagName = tag.getText();
        LOG.debug("rid={} expenseType={}", receiptUser.getRid(), tagName);
        List<ExpenseTagEntity> expenseTags = expensesService.getExpenseTags(receiptUser.getRid());
        List<ItemEntity> items = new ArrayList<>();

        if (!"Un-Assigned".equalsIgnoreCase(tagName)) {
            for (ExpenseTagEntity expenseTagEntity : expenseTags) {
                if (expenseTagEntity.getTagName().equalsIgnoreCase(tagName)) {
                    items = itemService.itemsForExpenseType(expenseTagEntity);
                    break;
                }
            }
        } else if ("Un-Assigned".equalsIgnoreCase(tagName)) {
            items = itemService.itemsForUnAssignedExpenseType(receiptUser.getRid());
        }

        expenseForm.setName(tagName);
        expenseForm.setExpenseTags(expenseTags);
        expenseForm.setItems(items);

        return nextPage;
    }
}
