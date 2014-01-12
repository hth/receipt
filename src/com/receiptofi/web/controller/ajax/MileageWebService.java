package com.receiptofi.web.controller.ajax;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.service.MileageService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.helper.json.Mileages;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
public class MileageWebService {

    private static Logger log = LoggerFactory.getLogger(MileageWebService.class);

    @Autowired private MileageService mileageService;

    /**
     * Helps load user mileage through ajax calls
     *
     * @param userSession
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/f.json", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String fetch(@ModelAttribute("userSession") UserSession userSession, HttpServletResponse httpServletResponse) throws IOException {
        DateTime time = DateUtil.now();

        if(userSession != null) {
            Mileages mileages = new Mileages();
            mileages.setMileages(mileageService.getMileageForThisMonth(userSession.getUserProfileId(), time));

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return mileages.asJson();
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }

    @RequestMapping(value = "/m.json", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String merge(@RequestBody String ids,
                  @ModelAttribute("userSession") UserSession userSession,
                  HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null && ids.length() > 0) {
            try {
                Map<String, String> map = jsonStringToMap(ids);
                MileageEntity mileageEntity = mileageService.merge(map.get("id1"), map.get("id2"), userSession.getUserProfileId());
                Mileages mileages = new Mileages();
                mileages.setMileages(mileageEntity);
                return mileages.asJson();
            } catch(Exception exception) {
                return "{\"success\" : false, \"message\" : \"" + exception.getLocalizedMessage() + "\"}";
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }

    private Map<String, String> jsonStringToMap(String ids) throws IOException {
        return new ObjectMapper().readValue(ids, new TypeReference<HashMap<String,String>>() {});
    }

    @RequestMapping(value = "/s.json", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String split(@RequestBody String id,
                 @ModelAttribute("userSession") UserSession userSession,
                 HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null && id.length() > 0) {
            try {
                List<MileageEntity> mileageEntities = mileageService.split(jsonStringToMap(id).get("id"), userSession.getUserProfileId());
                Mileages mileages = new Mileages();
                mileages.setMileages(mileageEntities);
                return mileages.asJson();
            } catch(Exception exception) {
                return "{\"success\" : false, \"message\" : \"" + exception.getLocalizedMessage() + "\"}";
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }
}
