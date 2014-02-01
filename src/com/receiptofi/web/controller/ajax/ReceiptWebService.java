package com.receiptofi.web.controller.ajax;

import com.receiptofi.domain.UserSession;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.FetcherService;
import com.receiptofi.service.LandingService;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.SHAHashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * User: hitender
 * Date: 4/19/13
 * Time: 11:44 PM
 */
@Controller
@RequestMapping(value = "/rws")
@SessionAttributes({"userSession"})
public class ReceiptWebService {
    private static final Logger log = LoggerFactory.getLogger(ReceiptWebService.class);

    @Autowired private FetcherService fetcherService;
    @Autowired private LandingService landingService;
    @Autowired private DocumentUpdateService documentUpdateService;

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
    Set<String> searchBiz(@RequestParam("term") String bizName,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findDistinctBizName(StringUtils.stripToEmpty(bizName));
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
    Set<String> searchBiz(@RequestParam("term") String bizAddress, @RequestParam("nameParam") String bizName,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findDistinctBizAddress(StringUtils.stripToEmpty(bizAddress), StringUtils.stripToEmpty(bizName));
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
    Set<String> searchPhone(@RequestParam("term") String bizPhone, @RequestParam("nameParam") String bizName, @RequestParam("addressParam") String bizAddress,
                             @ModelAttribute("userSession") UserSession userSession,
                             HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findDistinctBizPhone(StringUtils.stripToEmpty(bizPhone), StringUtils.stripToEmpty(bizAddress), StringUtils.stripToEmpty(bizName));
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
    Set<String> searchItem(@RequestParam("term") String itemName, @RequestParam("nameParam") String bizName,
                            @ModelAttribute("userSession") UserSession userSession,
                            HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return fetcherService.findDistinctItems(StringUtils.stripToEmpty(itemName), StringUtils.stripToEmpty(bizName));
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
     * Gets all the pending receipt after a receipt is successfully uploaded
     *
     * @param userSession
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/pending", method = RequestMethod.POST)
    public @ResponseBody
    long pendingReceipts(@ModelAttribute("userSession") UserSession userSession,
                         HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            return landingService.pendingReceipt(userSession.getUserProfileId());
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return -1L;
        }
    }

    /**
     * Check if a duplicate receipt exists for the user
     *
     * @param date
     * @param total
     * @param userProfileId
     * @param userSession
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    //TODO make this post
    @RequestMapping(value = "/check_for_duplicate", method = RequestMethod.GET)
    //@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicate Account")  // 409 //TODO something to think about
    public @ResponseBody
    boolean checkForDuplicate(@RequestParam("date") String date, @RequestParam("total") String total,
                              @RequestParam("userProfileId") String userProfileId,
                              @ModelAttribute("userSession") UserSession userSession,
                              HttpServletResponse httpServletResponse) throws IOException, ParseException, NumberFormatException {

        if(userSession != null) {
            try {
                Date receiptDate = DateUtil.getDateFromString(StringUtils.stripToEmpty(date));
                Double receiptTotal = Formatter.getCurrencyFormatted(StringUtils.stripToEmpty(total)).doubleValue();

                String checkSum = SHAHashing.calculateChecksumForNotDeleted(StringUtils.stripToEmpty(userProfileId), receiptDate, receiptTotal);
                return documentUpdateService.hasReceiptWithSimilarChecksum(checkSum);
            } catch(ParseException parseException) {
                log.error("Ajax checkForDuplicate failed to parse total: " + parseException.getLocalizedMessage());
                throw parseException;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return true;
        }
    }

    /**
     * Update the orientation of the image
     *
     * @param fileSystemId
     * @param imageOrientation
     * @param blobId
     * @param userProfileId
     * @param userSession
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/change_fs_image_orientation", method = RequestMethod.POST)
    public @ResponseBody
    boolean changeFSImageOrientation(@RequestParam("fileSystemId") String fileSystemId,
                                     @RequestParam("orientation") String imageOrientation,
                                     @RequestParam("blobId") String blobId,
                                     @RequestParam("userProfileId") String userProfileId,
                                     @ModelAttribute("userSession") UserSession userSession,
                                     HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue() || userProfileId.equalsIgnoreCase(userSession.getUserProfileId())) {
                try {
                    fetcherService.changeFSImageOrientation(
                            StringUtils.stripToEmpty(fileSystemId),
                            Integer.parseInt(StringUtils.stripToEmpty(imageOrientation)),
                            blobId
                    );
                    return true;
                } catch (Exception e) {
                    //Eat the error message
                    log.error("Failed to change orientation of the image: " + e.getLocalizedMessage());
                    return false;
                }
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return false;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return false;
        }
    }
}
