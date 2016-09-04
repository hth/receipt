package com.receiptofi.web.controller.access;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.service.LandingService;
import com.receiptofi.web.form.ReportAnalysisForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * User: hitender
 * Date: 2/5/15 2:36 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/reportAnalysis")
public class ReportAnalysisController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportAnalysisController.class);

    @Value ("${ReportAnalysisController.nextPage:/reportAnalysis}")
    private String nextPage;

    private final LandingService landingService;

    @Autowired
    public ReportAnalysisController(LandingService landingService) {
        this.landingService = landingService;
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("reportAnalysisForm")
            ReportAnalysisForm reportAnalysisForm
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        /** Lists all the receipt grouped by months. */
        List<ReceiptGrouped> groupedByMonth = landingService.getReceiptGroupedByMonth(receiptUser.getRid());
        if (groupedByMonth.size() >= 3) {
            reportAnalysisForm.setReceiptGroupedByMonths(groupedByMonth);
        } else {
            reportAnalysisForm.setReceiptGroupedByMonths(landingService.addMonthsIfLessThanThree(groupedByMonth, groupedByMonth.size()));
        }

        if (!groupedByMonth.isEmpty()) {
            reportAnalysisForm.setReceiptListViews(landingService.getReceiptsForMonths(receiptUser.getRid(), groupedByMonth));
        }

        /** Used for charting in Expense Analysis tab */
        LOG.info("Calculating Pie chart - item expense");
        reportAnalysisForm.setThisYearExpenseByTags(landingService.getAllItemExpenseForTheYear(receiptUser.getRid()));

        reportAnalysisForm.setItemsForYear(Calendar.getInstance().get(Calendar.YEAR));
        return nextPage;
    }
}
