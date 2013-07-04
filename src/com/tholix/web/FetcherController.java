package com.tholix.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.FetcherService;
import com.tholix.service.LandingService;

/**
 * User: hitender
 * Date: 4/19/13
 * Time: 11:44 PM
 */
@Controller
@RequestMapping(value = "/fetcher")
@SessionAttributes({"userSession"})
public class FetcherController {
    private static final Logger log = Logger.getLogger(FetcherController.class);

    @Autowired FetcherService fetcherService;
    @Autowired LandingService landingService;

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param bizName
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/find_company", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchBiz(@RequestParam("term") String bizName,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findBizName(bizName);
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
    }

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param bizAddress
     * @param bizName
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/find_address", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchBiz(@RequestParam("term") String bizAddress, @RequestParam("nameParam") String bizName,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findBizAddress(bizAddress, bizName);
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
    }

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param bizPhone
     * @param bizName
     * @param bizAddress
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/find_phone", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchPhone(@RequestParam("term") String bizPhone, @RequestParam("nameParam") String bizName, @RequestParam("addressParam") String bizAddress,
                             @ModelAttribute("userSession") UserSession userSession,
                             HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findBizPhone(bizPhone, bizAddress, bizName);
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
    }

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param itemName
     * @param bizName
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/find_item", method = RequestMethod.GET)
    public @ResponseBody
    List<String> searchItem(@RequestParam("term") String itemName, @RequestParam("nameParam") String bizName,
                            @ModelAttribute("userSession") UserSession userSession,
                            HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findItems(itemName, bizName);
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
    }

    @RequestMapping(value = "/pending", method = RequestMethod.POST)
    public @ResponseBody
    long pendingReceipts(@ModelAttribute("userSession") UserSession userSession, HttpServletResponse httpServletResponse)
            throws IOException {

        if(userSession != null) {
            return landingService.pendingReceipt(userSession.getUserProfileId());
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return -1;
        }
    }
}
