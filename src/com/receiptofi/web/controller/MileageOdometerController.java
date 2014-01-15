package com.receiptofi.web.controller;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.service.MileageService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.MileageForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 1/13/14 8:25 AM
 */
@Controller
@RequestMapping(value = "/modv")
@SessionAttributes({"userSession"})
public class MileageOdometerController {
    private static final Logger log = LoggerFactory.getLogger(LandingController.class);

    @Autowired private MileageService mileageService;

    @Value("${MOD_VIEW:/mileage}")
    private String NEXT_PAGE;

    @RequestMapping(value = "/{mileageId}", method = RequestMethod.GET)
    public ModelAndView loadForm(@PathVariable String mileageId, @ModelAttribute("mileageForm") MileageForm mileageForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        log.info("Loading MileageEntity with id: " + mileageId);

        MileageEntity mileageEntity = mileageService.getMileage(mileageId, userSession.getUserProfileId());
        if(mileageEntity != null) {
            mileageForm.setMileage(mileageEntity);
        } else {
            //TODO check all get methods that can result in display sensitive data of other users to someone else fishing
            //Possible condition of bookmark or trying to gain access to some unknown receipt
            log.warn("User " + userSession.getUserProfileId() + ", tried submitting an invalid receipt id: " + mileageId);
        }

        ModelAndView modelAndView = new ModelAndView(NEXT_PAGE);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
    }

}
