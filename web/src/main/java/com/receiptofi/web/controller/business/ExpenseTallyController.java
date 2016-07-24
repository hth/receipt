package com.receiptofi.web.controller.business;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.ExpenseTallyService;
import com.receiptofi.web.form.business.UserExpenseTallyForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: hitender
 * Date: 7/23/16 9:26 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business/expenseTally")
public class ExpenseTallyController {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTallyController.class);

    private String nextPage;
    private ExpenseTallyService expenseTallyService;

    @Autowired
    public ExpenseTallyController(
            @Value ("${nextPage:/business/expenseTally}")
            String nextPage,

            ExpenseTallyService expenseTallyService) {
        this.nextPage = nextPage;
        this.expenseTallyService = expenseTallyService;
    }

    @RequestMapping (method = RequestMethod.GET)
    public String loadForm(@ModelAttribute ("userExhibitExpensesForm") UserExpenseTallyForm userExpenseTallyForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
        userExpenseTallyForm.setExpenseTallys(expenseTallyService.getUsersForExpenseTally(receiptUser.getRid()));
        return nextPage;
    }
}
