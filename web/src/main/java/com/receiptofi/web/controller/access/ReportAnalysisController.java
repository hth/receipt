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
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
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

    @Autowired
    private LandingService landingService;

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (method = RequestMethod.GET)
    public ModelAndView loadForm(
            @ModelAttribute ("reportAnalysisForm")
            ReportAnalysisForm reportAnalysisForm
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ModelAndView modelAndView = new ModelAndView(nextPage);

        /** Lists all the receipt grouped by months */
        List<ReceiptGrouped> groupedByMonth = landingService.getAllObjectsGroupedByMonth(receiptUser.getRid());
        if (groupedByMonth.size() >= 3) {
            modelAndView.addObject("months", groupedByMonth);
        } else {
            modelAndView.addObject("months", landingService.addMonthsIfLessThanThree(groupedByMonth, groupedByMonth.size()));
        }
        reportAnalysisForm.setReceiptGroupedByMonths(groupedByMonth);

        return modelAndView;
    }
}
