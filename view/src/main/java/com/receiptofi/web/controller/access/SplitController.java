package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.json.JsonOweExpenses;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.ConnectionTypeEnum;
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

import java.util.List;

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
    public String split(
            @ModelAttribute ("splitForm")
            SplitForm splitForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<JsonOweExpenses> jsonOweMe = splitExpensesService.getJsonOweExpenses(receiptUser.getRid());
        splitForm.setJsonOweMe(jsonOweMe);

        for (JsonOweExpenses jsonOweExpense : splitForm.getJsonOweMe()) {
            splitForm.addYourSplitExpenses(
                    userProfilePreferenceService.findByReceiptUserId(jsonOweExpense.getFriendUserId()).getName(),
                    splitExpensesService.getSplitExpenses(receiptUser.getRid(), jsonOweExpense.getFriendUserId()));
        }

        List<JsonOweExpenses> jsonOweOthers = splitExpensesService.getJsonOweOthersExpenses(receiptUser.getRid());
        splitForm.setJsonOweOthers(jsonOweOthers);

        for (JsonOweExpenses jsonOweExpense : splitForm.getJsonOweOthers()) {
            splitForm.addFriendsSplitExpenses(
                    userProfilePreferenceService.findByReceiptUserId(jsonOweExpense.getReceiptUserId()).getName(),
                    splitExpensesService.getSplitExpenses(jsonOweExpense.getReceiptUserId(), receiptUser.getRid()));
        }

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
            ConnectionTypeEnum connectionType
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(
                LandingController.SUCCESS,
                friendService.updateConnection(
                        id.getText(),
                        auth.getText(),
                        connectionType,
                        receiptUser.getRid()));
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
            ScrubbedInput mail
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LandingController.SUCCESS, friendService.unfriend(receiptUser.getRid(), mail.getText()));
        return jsonObject.toString();
    }

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/settle",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String settle(
            @RequestParam ("id")
            ScrubbedInput id
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SplitExpensesEntity splitExpenses = splitExpensesService.getById(id.getText(), receiptUser.getRid());

        SplitExpensesEntity splitToSettle = splitExpensesService.findSplitExpensesToSettle(
                splitExpenses.getFriendUserId(),
                receiptUser.getRid(),
                splitExpenses.getSplitTotal());

        if (null == splitToSettle) {
            LOG.warn("Could not settle expenses as null id={} rid={}", id.getText(), receiptUser.getRid());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("st", 0.00);
            jsonObject.addProperty(LandingController.SUCCESS, false);
            return jsonObject.toString();
        }

        splitExpensesService.settleSplitExpenses(splitExpenses, splitToSettle);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("st", splitExpenses.getSplitTotal());
        jsonObject.addProperty(LandingController.SUCCESS, true);
        return jsonObject.toString();
    }
}
