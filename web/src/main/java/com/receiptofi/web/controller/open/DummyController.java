package com.receiptofi.web.controller.open;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.json.JsonExpenseTag;
import com.receiptofi.domain.json.JsonReceipt;
import com.receiptofi.domain.json.JsonReceiptItem;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.web.rest.JsonReceiptDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 12/30/14 6:40 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/receipt")
public class DummyController {
    private static final Logger LOG = LoggerFactory.getLogger(DummyController.class);

    @Autowired private ReceiptService receiptService;
    @Autowired private ItemService itemService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

    @RequestMapping (value = "/rest/{receiptId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonReceiptDetail loadReceipt(
            @PathVariable
            String receiptId
    ) {
        JsonReceiptDetail jsonReceiptDetail = new JsonReceiptDetail();
        LOG.info("Loading Receipt Item with id={}", receiptId);

        ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId, "10000000006");
        if (null == receiptEntity) {
            LOG.warn("User={}, tried submitting an invalid receipt={}", "10000000006", receiptId);
        } else {
            List<ItemEntity> items = itemService.getAllItemsOfReceipt(receiptEntity.getId());
            List<ExpenseTagEntity> expenseTypes = userProfilePreferenceService.activeExpenseTypes("10000000006");

            jsonReceiptDetail.setJsonReceipt(new JsonReceipt(receiptEntity));

            List<JsonReceiptItem> jsonReceiptItems = new LinkedList<>();
            for(ItemEntity itemEntity : items) {
                JsonReceiptItem jsonReceiptItem = JsonReceiptItem.newInstance(itemEntity);
                jsonReceiptItems.add(jsonReceiptItem);
            }
            jsonReceiptDetail.setItems(jsonReceiptItems);

            List<JsonExpenseTag> jsonExpenseTags = new ArrayList<>();
            for(ExpenseTagEntity expenseTagEntity : expenseTypes) {
                jsonExpenseTags.add(JsonExpenseTag.newInstance(expenseTagEntity));
            }
            jsonReceiptDetail.setJsonExpenseTags(jsonExpenseTags);


            LOG.info("populate receipt json for id={}", receiptId);
        }
        return jsonReceiptDetail;
    }
}
