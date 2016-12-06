package com.receiptofi.web.controller.display;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.service.DocumentDailyStatService;
import com.receiptofi.service.DocumentPendingService;
import com.receiptofi.web.rest.DocumentDailyStat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Displays processed documents.
 *
 * User: hitender
 * Date: 11/18/14 10:29 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/display")
public class DisplayLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayLandingController.class);

    @Autowired private DocumentPendingService documentPendingService;
    @Autowired private DocumentDailyStatService documentDailyStatService;

    /**
     * Refers to landing.jsp
     */
    @Value ("${nextPage:/display/landing}")
    private String nextPage;

    @Value ("${numberOfDays:45}")
    private int numberOfDays;

    @PreAuthorize ("hasRole('ROLE_ANALYSIS_READ')")
    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET
    )
    public ModelAndView loadForm() {
        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("processedToday", documentPendingService.getTotalProcessedToday());
        modelAndView.addObject("pending", documentPendingService.getTotalPending());
        return modelAndView;
    }

    @PreAuthorize ("hasRole('ROLE_ANALYSIS_READ')")
    @RequestMapping (value = "/loadStats.json", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public DocumentDailyStat loadHistoricalStats() {
        List<DocumentDailyStatEntity> dailyStatsEntities = documentDailyStatService.getDailyStatForDays(numberOfDays);
        return new DocumentDailyStat(dailyStatsEntities);
    }

    @PreAuthorize ("hasRole('ROLE_ANALYSIS_READ')")
    @RequestMapping (value = "/documentProcessingPace.json", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String processingPace() {
        JsonObject json = new JsonObject();
        json.addProperty("processedToday", documentPendingService.getTotalProcessedToday());
        json.addProperty("pending", documentPendingService.getTotalPending());

        return new Gson().toJson(json);
    }
}
