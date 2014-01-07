package com.receiptofi.web.controller.ajax;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.service.MileageService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.controller.BaseController;
import com.receiptofi.web.helper.json.Mileages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 1/6/14 12:05 AM
 */
@Controller
@RequestMapping(value = "/mws")
@SessionAttributes({"userSession"})
public class MileageWebService extends BaseController {

    private static Logger log = LoggerFactory.getLogger(MileageWebService.class);

    @Autowired private MileageService mileageService;

    /**
     * Helps load user mileage through ajax calls
     *
     * @param profileId
     * @param authKey
     * @return
     */
    @RequestMapping(value = "/{profileId}/auth/{authKey}.json", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody
    String loadJSON(@PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("JSON : " + profileId);

        UserProfileEntity userProfile = authenticate(profileId, authKey);
        if(userProfile != null) {

            Mileages mileages = new Mileages();
            mileages.setMileages(mileageService.getMileageForThisMonth(profileId, time));

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return mileages.asJson();
        } else {
            return "{}";
        }
    }
}
