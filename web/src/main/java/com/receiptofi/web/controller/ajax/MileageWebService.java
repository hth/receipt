package com.receiptofi.web.controller.ajax;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.MileageService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.helper.json.Driven;
import com.receiptofi.web.helper.json.MileageDateUpdateResponse;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 1/6/14 12:05 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/ws/m")
public class MileageWebService {
    private static final Logger LOG = LoggerFactory.getLogger(MileageWebService.class);

    @Autowired private MileageService mileageService;

    /**
     * Helps load user mileage through ajax calls.
     *
     * @return
     * @throws IOException
     */
    @RequestMapping (
            value = "/f.json",
            method = RequestMethod.POST,
            produces = "application/json")
    public String fetch() throws IOException {
        DateTime time = DateUtil.now();

        Driven driven = new Driven();
        driven.setMiles(
                mileageService.getMileageForThisMonth(
                        ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid(),
                        time
                )
        );
        return driven.asJson();
    }

    @RequestMapping (
            value = "/m.json",
            method = RequestMethod.POST,
            produces = "application/json")
    public String merge(@RequestBody String ids, HttpServletResponse httpServletResponse) throws IOException {
        if (ids.length() > 0) {
            try {
                Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(ids);
                MileageEntity mileageEntity = mileageService.merge(
                        map.get("id1").getText(),
                        map.get("id2").getText(),
                        ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid()
                );
                Driven driven = new Driven();
                driven.setMileages(mileageEntity);
                driven.setMonthlyMileage(
                        mileageService.monthlyTotal(
                                ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid(),
                                DateUtil.now()
                        )
                );
                return driven.asJson();
            } catch (Exception exception) {
                return createJSONUsingMileageDateUpdateResponse(false, exception.getLocalizedMessage());
            }
        } else {
            LOG.warn("Access denied.", SC_FORBIDDEN);
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }

    @RequestMapping (
            value = "/s.json",
            method = RequestMethod.POST,
            produces = "application/json")
    public String split(@RequestBody String id, HttpServletResponse httpServletResponse) throws IOException {
        if (id.length() > 0) {
            try {
                List<MileageEntity> mileageEntities = mileageService.split(
                        ParseJsonStringToMap.jsonStringToMap(id).get("id").getText(),
                        ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid()
                );
                Driven driven = new Driven();
                driven.setMiles(mileageEntities);
                driven.setMonthlyMileage(
                        mileageService.monthlyTotal(
                                ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid(),
                                DateUtil.now()
                        )
                );
                return driven.asJson();
            } catch (Exception exception) {
                return createJSONUsingMileageDateUpdateResponse(false, exception.getLocalizedMessage());
            }
        } else {
            LOG.warn("Access denied.", SC_FORBIDDEN);
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }

    @RequestMapping (
            value = "/msd",
            method = RequestMethod.POST,
            headers = "Accept=application/json")
    public String updateMileageStartDate(@RequestBody String mileageInfo, HttpServletResponse httpServletResponse) throws IOException {
        if (mileageInfo.length() > 0) {
            Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(mileageInfo);
            try {
                MileageEntity mileageEntity = mileageService.getMileage(
                        map.get("id").getText(),
                        ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid()
                );
                if (null == mileageEntity) {
                    return createJSONUsingMileageDateUpdateResponse(
                            false,
                            "Failed to update trip start date as record below no longer exists. Please hit browser refresh."
                    );
                } else {
                    boolean status = mileageService.updateStartDate(
                            map.get("id").getText(),
                            StringUtils.remove(map.get("msd").getText(), "\""),
                            ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid()
                    );
                    if (status) {
                        return createJSONUsingMileageDateUpdateResponse(true, mileageEntity);
                    } else {
                        return createJSONUsingMileageDateUpdateResponse(false, "Failed to update trip start date");
                    }
                }
            } catch (RuntimeException re) {
                return createJSONUsingMileageDateUpdateResponse(false, "Failed to update trip start date");
            }

        } else {
            LOG.warn("Access denied.", SC_FORBIDDEN);
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }

    @RequestMapping (
            value = "/med",
            method = RequestMethod.POST,
            headers = "Accept=application/json")
    public String updateMileageEndDate(@RequestBody String mileageInfo, HttpServletResponse httpServletResponse) throws IOException {

//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.add("Content-Type", "application/json;charset=UTF-8");
//        return new ResponseEntity<>(mileageDateUpdateResponse.asJson(), responseHeaders, HttpStatus.OK);

        if (mileageInfo.length() > 0) {
            Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(mileageInfo);
            try {
                String rid = ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid();
                MileageEntity mileageEntity = mileageService.getMileage(map.get("id").getText(), rid);
                if (null == mileageEntity) {
                    return createJSONUsingMileageDateUpdateResponse(
                            false,
                            "Failed to update trip end date as record below no longer exists. Please hit browser refresh."
                    );
                } else {
                    if (mileageEntity.isComplete()) {
                        boolean status = mileageService.updateEndDate(map.get("id").getText(), StringUtils.remove(map.get("med").getText(), "\""), rid);
                        if (status) {
                            return createJSONUsingMileageDateUpdateResponse(status, mileageEntity);
                        } else {
                            return createJSONUsingMileageDateUpdateResponse(status, "Failed to update trip end date");
                        }
                    } else {
                        return createJSONUsingMileageDateUpdateResponse(
                                false,
                                "Failed to update trip end date. Record no longer represent mileage driven. Please hit browser refresh."
                        );
                    }
                }
            } catch (RuntimeException re) {
                return createJSONUsingMileageDateUpdateResponse(false, "Failed to update trip end date");
            }
        } else {
            LOG.warn("Access denied.", SC_FORBIDDEN);
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return "{}";
        }
    }

    private String createJSONUsingMileageDateUpdateResponse(boolean status, MileageEntity mileageEntity) {
        MileageDateUpdateResponse mileageDateUpdateResponse = new MileageDateUpdateResponse();
        mileageDateUpdateResponse.setSuccess(status);
        mileageDateUpdateResponse.setDays(mileageEntity.tripDays());
        return mileageDateUpdateResponse.asJson();
    }

    private String createJSONUsingMileageDateUpdateResponse(boolean status, String message) {
        MileageDateUpdateResponse mileageDateUpdateResponse = new MileageDateUpdateResponse();
        mileageDateUpdateResponse.setSuccess(status);
        mileageDateUpdateResponse.setMessage(message);
        return mileageDateUpdateResponse.asJson();
    }
}
