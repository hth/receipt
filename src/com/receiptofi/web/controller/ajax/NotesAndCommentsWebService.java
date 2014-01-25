package com.receiptofi.web.controller.ajax;

import com.receiptofi.domain.UserSession;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.MileageService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.TextInputScrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

/**
 * Update for all Ajax Calls
 *
 * User: hitender
 * Date: 7/22/13
 * Time: 8:57 PM
 */
@Controller
@RequestMapping(value = "/ncws")
@SessionAttributes({"userSession"})
public class NotesAndCommentsWebService {
     private static final Logger log = LoggerFactory.getLogger(NotesAndCommentsWebService.class);

    @Autowired ReceiptService receiptService;
    @Autowired MileageService mileageService;

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param body
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/rn", method = RequestMethod.POST, headers = "Accept=application/json")
    public @ResponseBody
    boolean receiptNotes(@RequestBody String body, @ModelAttribute("userSession") UserSession userSession,
                         HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null && body.length() > 0) {
            log.info("Receipt notes updated by userProfileId: " + userSession.getUserProfileId());
            Map<String, String> map = ParseJsonStringToMap.jsonStringToMap(body);
            return receiptService.updateReceiptNotes(TextInputScrubber.scrub(map.get("notes")), map.get("receiptId"), userSession.getUserProfileId());
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return false;
        }
    }

    @RequestMapping(value ="/umn", method = RequestMethod.POST, headers = "Accept=application/json")
    public @ResponseBody
    boolean updateMileageNotes(@RequestBody String body, @ModelAttribute("userSession") UserSession userSession,
                               HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null && body.length() > 0) {
            log.info("Note updated by userProfileId: " + userSession.getUserProfileId());
            Map<String, String> map = ParseJsonStringToMap.jsonStringToMap(body);
            return mileageService.updateMileageNotes(TextInputScrubber.scrub(map.get("notes")), map.get("mileageId"), userSession.getUserProfileId());
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return false;
        }
    }

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param body
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/rc", method = RequestMethod.POST, headers = "Accept=application/json")
    public @ResponseBody
    boolean receiptRecheckComment(@RequestBody String body, @ModelAttribute("userSession") UserSession userSession,
                                  HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            log.info("Receipt recheck comment updated by userProfileId: " + userSession.getUserProfileId());
            Map<String, String> map = ParseJsonStringToMap.jsonStringToMap(body);
            return receiptService.updateReceiptComment(TextInputScrubber.scrub(map.get("notes")), map.get("receiptId"), userSession.getUserProfileId());
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return false;
        }
    }

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param body
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/dc", method = RequestMethod.POST, headers = "Accept=application/json")
    public @ResponseBody
    boolean documentRecheckComment(@RequestBody String body, @ModelAttribute("userSession") UserSession userSession,
                                   HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                log.info("Document recheck comment updated by userProfileId: " + userSession.getUserProfileId());
                Map<String, String> map = ParseJsonStringToMap.jsonStringToMap(body);
                return receiptService.updateDocumentComment(TextInputScrubber.scrub(map.get("notes")), map.get("documentId"));
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
