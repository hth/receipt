package com.receiptofi.web.controller.display;

import com.receiptofi.service.CronStatsService;
import com.receiptofi.web.form.CronStatsForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: hitender
 * Date: 4/22/15 1:11 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/display")
public class ShowCronStats {
    private static final Logger LOG = LoggerFactory.getLogger(ShowCronStats.class);

    /**
     * Refers to landing.jsp
     */
    @Value ("${nextPage:/display/cronStats}")
    private String nextPage;

    @Value ("${limit:10}")
    private int limit;

    @Autowired private CronStatsService cronStatsService;

    @PreAuthorize ("hasRole('ROLE_ANALYSIS_READ')")
    @RequestMapping (
            value = "/cronStats",
            method = RequestMethod.GET
    )
    public String loadForm(
            @ModelAttribute ("cronStatsForm")
            CronStatsForm cronStatsForm
    ) {
        cronStatsForm.setTaskStats(cronStatsService.getUniqueCronTasks(limit));
        return nextPage;
    }
}
