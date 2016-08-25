/**
 *
 */
package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.json.JsonFriend;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.SplitActionEnum;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.FriendService;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.service.SplitExpensesService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.ReceiptByBizForm;
import com.receiptofi.web.form.ReceiptForm;
import com.receiptofi.web.helper.ReceiptForMonth;
import com.receiptofi.web.helper.ReceiptLandingView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hitender
 * @since Jan 1, 2013 11:55:19 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/receipt")
public class ReceiptController {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptController.class);

    @Value ("${ReceiptController.nextPage:/receipt}")
    private String nextPage;

    @Value ("${ReceiptController.nextPageByBiz:/receiptByBiz}")
    private String nextPageByBiz;

    private final ReceiptService receiptService;
    private final ItemService itemService;
    private final BizNameManager bizNameManager;
    private final ExpensesService expensesService;
    private final FriendService friendService;
    private final SplitExpensesService splitExpensesService;
    private final UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public ReceiptController(
            BizNameManager bizNameManager,
            UserProfilePreferenceService userProfilePreferenceService,
            ExpensesService expensesService,
            FriendService friendService,
            ItemService itemService,
            ReceiptService receiptService,
            SplitExpensesService splitExpensesService
    ) {
        this.bizNameManager = bizNameManager;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.expensesService = expensesService;
        this.friendService = friendService;
        this.itemService = itemService;
        this.receiptService = receiptService;
        this.splitExpensesService = splitExpensesService;
    }

    @RequestMapping (value = "/{receiptId}", method = RequestMethod.GET)
    public String loadForm(
            @PathVariable
            ScrubbedInput receiptId,

            @ModelAttribute ("receiptForm")
            ReceiptForm receiptForm
    ) {
        LOG.info("Loading Receipt Item with id={}", receiptId);
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReceiptEntity receipt = receiptService.findReceipt(receiptId.getText(), receiptUser.getRid());
        if (null == receipt) {
            LOG.warn("User={}, tried submitting an invalid receipt={}", receiptUser.getRid(), receiptId);
        } else {
            //TODO match with ReceiptMobileService.getReceiptAndItemUpdates
            String fetchReceiptId = null == receipt.getReferReceiptId() ? receipt.getId() : receipt.getReferReceiptId();
            List<ItemEntity> items = itemService.getAllItemsOfReceipt(fetchReceiptId);
            List<ExpenseTagEntity> expenseTags = expensesService.getExpenseTags(receiptUser.getRid());

            receiptForm.setReceipt(receipt);
            receiptForm.setItems(items);
            receiptForm.setExpenseTags(expenseTags);

            if (null == receipt.getReferReceiptId()) {
                /** Refers to original user accessing original receipt. */
                receiptForm.setJsonFriends(friendService.getFriends(receiptUser.getRid()));

                if (receipt.getSplitCount() > 1) {
                    receiptForm.setJsonSplitFriends(splitExpensesService.populateProfileOfFriends(
                            fetchReceiptId,
                            receiptForm.getJsonFriends()
                    ));
                }
            } else {
                /** Refers to split user accessing shared receipt. */
                ReceiptEntity originalReceipt = receiptService.findReceipt(receipt.getReferReceiptId());
                receiptForm.setJsonFriends(friendService.getFriends(originalReceipt.getReceiptUserId()));

                receiptForm.setJsonSplitFriends(splitExpensesService.populateProfileOfFriends(
                        fetchReceiptId,
                        receiptForm.getJsonFriends()
                ));

                receiptForm.getJsonSplitFriends().remove(new JsonFriend(receiptUser.getRid(), "", ""));
                receiptForm.getJsonSplitFriends().add(new JsonFriend(userProfilePreferenceService.findByReceiptUserId(originalReceipt.getReceiptUserId())));
            }

            LOG.debug("receiptForm={}", receiptForm);
        }
        return nextPage;
    }

    @RequestMapping (
            value = "/delete",
            method = RequestMethod.POST,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String deleteReceipt(
            @RequestBody
            String body
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        String receiptId = map.get("receiptId").getText();
        LOG.info("Delete receiptId={} rid={}", receiptId, receiptUser.getRid());

        JsonObject jsonObject = new JsonObject();
        try {
            boolean result = receiptService.deleteReceipt(receiptId, receiptUser.getRid());
            jsonObject.addProperty("result", result);
            if (!result) {
                jsonObject.addProperty("message", "Failed to Delete Receipt. This happens if receipt is being shared and is in middle of settling splits.");
            }
            /** Success message is set in JS. */
        } catch (RuntimeException e) {
            LOG.error("Error occurred during receipt delete receiptId={} rid={} reason={}",
                    receiptId, receiptUser.getRid(), e.getLocalizedMessage(), e);

            jsonObject.addProperty("result", false);
            jsonObject.addProperty("message", "Something went wrong while deleting receipt.");
        }
        return jsonObject.toString();
    }

    @RequestMapping (
            value = "/recheck",
            method = RequestMethod.POST,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String recheckReceipt(
            @RequestBody
            String body
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        String receiptId = map.get("receiptId").getText();
        LOG.info("Initiating re-check on receiptId={}", receiptId);

        JsonObject jsonObject = new JsonObject();
        try {
            boolean result = receiptService.recheck(receiptId, receiptUser.getRid());
            jsonObject.addProperty("result", result);
            if (!result) {
                jsonObject.addProperty("message", "Failed to Re-Check Receipt. This happens if receipt is being shared and is in middle of settling splits.");
            }
            /** Success message is set in JS. */
        } catch (Exception e) {
            LOG.error("Error occurred during receipt recheck receiptId={} rid={} reason={}",
                    receiptId, receiptUser.getRid(), e.getLocalizedMessage(), e);

            jsonObject.addProperty("result", false);
            jsonObject.addProperty("message", e.getLocalizedMessage());
        }
        return jsonObject.toString();
    }

    /**
     * Finds all receipts with business name.
     *
     * @param bizName
     * @return
     */
    @RequestMapping (value = "/biz/{bizName}/{monthYear}", method = RequestMethod.GET)
    public String receiptByBizName(
            @PathVariable
            ScrubbedInput bizName,

            @PathVariable
            ScrubbedInput monthYear,

            @ModelAttribute ("receiptByBizForm")
            ReceiptByBizForm receiptByBizForm
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Loading Receipts by bizName={} monthYear={}", bizName, monthYear);
        receiptByBizForm.setMonthYear(monthYear.getText());
        receiptByBizForm.setBizName(bizName.getText());

        List<BizNameEntity> bizNames = bizNameManager.findAllBizWithMatchingName(bizName.getText());
        for (BizNameEntity bizNameEntity : bizNames) {
            List<ReceiptEntity> receipts = receiptService.findReceipt(
                    bizNameEntity,
                    receiptUser.getRid(),
                    ReceiptForMonth.MMM_YYYY.parseDateTime(monthYear.getText()));
            for (ReceiptEntity receiptEntity : receipts) {
                receiptByBizForm.getReceiptLandingViews().add(ReceiptLandingView.newInstance(receiptEntity));
            }
        }

        return nextPageByBiz;
    }

    /**
     * Original owner of the receipt can add or remove friends from split.
     *
     * @param fid
     * @param receiptId
     * @param splitAction
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/split",
            method = RequestMethod.POST,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String split(
            @RequestParam ("fid")
            ScrubbedInput fid,

            @RequestParam ("receiptId")
            ScrubbedInput receiptId,

            @RequestParam ("splitAction")
            SplitActionEnum splitAction,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.debug("Receipt id={} fid={} splitAction={}", receiptId, fid, splitAction);

        boolean result;
        Double splitTotal;

        ReceiptEntity receipt = receiptService.findReceipt(receiptId.getText(), receiptUser.getRid());
        if (null == receipt) {
            LOG.warn("No Receipt found to Split with receiptId={}", receiptId);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "NotFound");
            return null;
        } else {
            result = receiptService.splitAction(fid.getText(), splitAction, receipt);
            splitTotal = receipt.getSplitTotal();
        }

        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("result", result);
            if (result) {
                jsonObject.addProperty("splitTotal", splitTotal.toString());
            }
            /** Success message is set in JS. */
        } catch (Exception e) {
            LOG.error("Error occurred during receipt recheck receiptId={} rid={} reason={}",
                    receiptId, receiptUser.getRid(), e.getLocalizedMessage(), e);

            jsonObject.addProperty("result", false);
        }
        return jsonObject.toString();
    }
}
