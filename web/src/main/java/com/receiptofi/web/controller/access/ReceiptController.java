/**
 *
 */
package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.json.JsonExpenseTag;
import com.receiptofi.domain.json.JsonReceipt;
import com.receiptofi.domain.json.JsonReceiptItem;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.ReceiptByBizForm;
import com.receiptofi.web.form.ReceiptForm;
import com.receiptofi.web.helper.ReceiptForMonth;
import com.receiptofi.web.helper.ReceiptLandingView;
import com.receiptofi.web.rest.JsonReceiptDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired private ReceiptService receiptService;
    @Autowired private ItemService itemService;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private ExpensesService expensesService;

    @RequestMapping (value = "/{receiptId}", method = RequestMethod.GET)
    public String loadForm(
            @PathVariable
            String receiptId,

            @ModelAttribute ("receiptForm")
            ReceiptForm receiptForm
    ) {
        LOG.info("Loading Receipt Item with id={}", receiptId);
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReceiptEntity receipt = receiptService.findReceipt(receiptId, receiptUser.getRid());
        if (null == receipt) {
            LOG.warn("User={}, tried submitting an invalid receipt={}", receiptUser.getRid(), receiptId);
        } else {
            List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());
            List<ExpenseTagEntity> expenseTags = expensesService.getExpenseTags(receiptUser.getRid());

            receiptForm.setReceipt(receipt);
            receiptForm.setItems(items);
            receiptForm.setExpenseTags(expenseTags);
            LOG.debug("receiptForm={}", receiptForm);
        }
        return nextPage;
    }

    /**
     * This method is not being used. Was suppose to support JSON representation of receipt data.
     *
     * @param receiptId
     * @return
     */
    @Deprecated
    @RequestMapping (value = "/rest/{receiptId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonReceiptDetail loadReceipt(
            @PathVariable
            String receiptId
    ) {
        JsonReceiptDetail jsonReceiptDetail = new JsonReceiptDetail();
        LOG.info("Loading Receipt Item with id={}", receiptId);
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReceiptEntity receipt = receiptService.findReceipt(receiptId, receiptUser.getRid());
        if (null == receipt) {
            LOG.warn("User={}, tried submitting an invalid receipt={}", receiptUser.getRid(), receiptId);
        } else {
            List<ItemEntity> items = itemService.getAllItemsOfReceipt(receipt.getId());
            List<ExpenseTagEntity> expenseTags = expensesService.getExpenseTags(receiptUser.getRid());

            jsonReceiptDetail.setJsonReceipt(new JsonReceipt(receipt));
            jsonReceiptDetail.setItems(items.stream().map(JsonReceiptItem::newInstance).collect(Collectors.toCollection(LinkedList::new)));
            jsonReceiptDetail.setJsonExpenseTags(expenseTags.stream().map(JsonExpenseTag::newInstance).collect(Collectors.toList()));
            LOG.info("populate receipt json for id={}", receiptId);
        }
        return jsonReceiptDetail;
    }

    @RequestMapping (
            value = "/delete",
            method = RequestMethod.POST,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String deleteExpenseTag(@RequestBody String body) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        String receiptId = map.get("receiptId").getText();
        LOG.info("Delete receiptId={}", receiptId);

        JsonObject jsonObject = new JsonObject();
        try {
            boolean result = receiptService.deleteReceipt(receiptId, receiptUser.getRid());
            jsonObject.addProperty("result", result);
            if (!result) {
                jsonObject.addProperty("message", "Failed to deleted receipt.");
            }
            /** Success message is set in JS. */
        } catch (Exception e) {
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
    public String recheck(@RequestBody String body) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        String receiptId = map.get("receiptId").getText();
        LOG.info("Initiating re-check on receiptId={}", receiptId);

        JsonObject jsonObject = new JsonObject();
        try {
            receiptService.reopen(receiptId, receiptUser.getRid());
            jsonObject.addProperty("result", true);
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
            String bizName,

            @PathVariable
            String monthYear,

            @ModelAttribute ("receiptByBizForm")
            ReceiptByBizForm receiptByBizForm
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Loading Receipts by Biz Name id={}", bizName);
        receiptByBizForm.setMonthYear(monthYear);
        receiptByBizForm.setBizName(bizName);

        List<BizNameEntity> bizNames = bizNameManager.findAllBizWithMatchingName(bizName);
        for (BizNameEntity bizNameEntity : bizNames) {
            List<ReceiptEntity> receipts = receiptService.findReceipt(
                    bizNameEntity,
                    receiptUser.getRid(),
                    ReceiptForMonth.MMM_YYYY.parseDateTime(monthYear));
            for (ReceiptEntity receiptEntity : receipts) {
                receiptByBizForm.getReceiptLandingViews().add(ReceiptLandingView.newInstance(receiptEntity));
            }
        }

        return nextPageByBiz;
    }
}
