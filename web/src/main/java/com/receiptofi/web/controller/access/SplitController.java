package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.json.JsonOweExpenses;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.FriendConnectionTypeEnum;
import com.receiptofi.service.FriendService;
import com.receiptofi.service.SplitExpensesService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.SplitForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 9/14/15 6:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/split")
public class SplitController {
    private static final Logger LOG = LoggerFactory.getLogger(SplitController.class);

    @Autowired private FriendService friendService;
    @Autowired private SplitExpensesService splitExpensesService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

    /**
     * Refers to split.jsp
     */
    @Value ("${nextPage:/split}")
    private String nextPage;

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("splitForm")
            SplitForm splitForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<JsonOweExpenses> jsonOweMe = new ArrayList<>();
        List<SplitExpensesEntity> splitExpenses = splitExpensesService.getOwesMe(receiptUser.getRid());
        for (SplitExpensesEntity splitExpense : splitExpenses) {
            if (splitExpense.getSplitTotal() > 0) {
                UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(splitExpense.getFriendUserId());
                JsonOweExpenses jsonOweExpense = new JsonOweExpenses(
                        splitExpense.getReceiptUserId(),
                        splitExpense.getFriendUserId(),
                        splitExpense.getSplitTotal(),
                        userProfile.getName());

                jsonOweMe.add(jsonOweExpense);
            }
        }
        splitForm.setJsonOweMe(jsonOweMe);

        List<JsonOweExpenses> jsonOweOthers = new ArrayList<>();
        splitExpenses = splitExpensesService.getOwesOthers(receiptUser.getRid());
        for (SplitExpensesEntity splitExpense : splitExpenses) {
            if (splitExpense.getSplitTotal() > 0) {
                UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(splitExpense.getReceiptUserId());
                JsonOweExpenses jsonOweExpense = new JsonOweExpenses(
                        splitExpense.getReceiptUserId(),
                        splitExpense.getFriendUserId(),
                        splitExpense.getSplitTotal(),
                        userProfile.getName());

                jsonOweOthers.add(jsonOweExpense);
            }
        }
        splitForm.setJsonOweOthers(jsonOweOthers);

        splitForm.setActiveProfiles(friendService.getActiveConnections(receiptUser.getRid()));
        splitForm.setPendingProfiles(friendService.getPendingConnections(receiptUser.getRid()));
        splitForm.setAwaitingProfiles(friendService.getAwaitingConnections(receiptUser.getRid()));

        return nextPage;
    }

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/friend",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String friendAccept(
            @RequestParam ("id")
            ScrubbedInput id,

            @RequestParam ("auth")
            ScrubbedInput auth,

            @RequestParam ("ct")
            FriendConnectionTypeEnum friendConnectionType,

            HttpServletResponse httpServletResponse
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean response;
        switch (friendConnectionType) {
            case A:
                /** Accept connection. */
                response = friendService.updateResponse(id.getText(), auth.getText(), true, receiptUser.getRid());
                break;
            case C:
                /** Cancel invitation to friend by removing AUTH id. */
                response = friendService.cancelInvite(id.getText(), auth.getText());
                break;
            case D:
                /** Decline connection. */
                response = friendService.updateResponse(id.getText(), auth.getText(), false, receiptUser.getRid());
                break;
            default:
                LOG.error("FriendConnectionType={} not defined", friendConnectionType);
                throw new UnsupportedOperationException("FriendConnectionType not supported " + friendConnectionType);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LandingController.SUCCESS, response);
        return jsonObject.toString();
    }

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/unfriend",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String unfriend(
            @RequestParam ("mail")
            ScrubbedInput mail,

            HttpServletResponse httpServletResponse
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LandingController.SUCCESS, friendService.unfriend(receiptUser.getRid(), mail.getText()));
        return jsonObject.toString();
    }
}
