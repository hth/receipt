package com.tholix.web.controller.ajax;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

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
import com.tholix.service.ReceiptService;

/**
 * Update for all Ajax Calls
 *
 * User: hitender
 * Date: 7/22/13
 * Time: 8:57 PM
 */
@Controller
@RequestMapping(value = "/modify")
@SessionAttributes({"userSession"})
public class ModifyController {


    @Autowired ReceiptService receiptService;

    /**
     * Note: UserSession parameter is to make sure no outside get requests are processed.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param notes
     * @param receiptId
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/receipt_notes", method = RequestMethod.GET)
    public @ResponseBody
    boolean receiptNotes(@RequestParam("term") String notes, @RequestParam("nameParam") String receiptId,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            return receiptService.updateNotes(notes, receiptId, userSession.getUserProfileId());
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
     * @param comment
     * @param receiptId
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/receipt_recheckComment", method = RequestMethod.GET)
    public @ResponseBody
    boolean receiptRecheckComment(@RequestParam("term") String comment, @RequestParam("nameParam") String receiptId,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            return receiptService.updateComment(comment, receiptId, userSession.getUserProfileId());
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
     * @param comment
     * @param receiptOCRId
     * @param userSession
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/receiptOCR_recheckComment", method = RequestMethod.GET)
    public @ResponseBody
    boolean searchBiz(@RequestParam("term") String comment, @RequestParam("nameParam") String receiptOCRId,
                           @ModelAttribute("userSession") UserSession userSession,
                           HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel().value >= UserLevelEnum.TECHNICIAN.getValue()) {
                return receiptService.updateOCRComment(comment, receiptOCRId);
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
