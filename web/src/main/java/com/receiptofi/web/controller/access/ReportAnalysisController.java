package com.receiptofi.web.controller.access;

import com.receiptofi.web.form.ExpenseTypeForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

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

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (method = RequestMethod.GET)
    public ModelAndView loadForm(
            Model model
    ) throws IOException {
        return new ModelAndView(nextPage);
    }
}
