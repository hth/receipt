/**
 *
 */
package com.receiptofi.web.controller.access;

import com.receiptofi.domain.BizNameEntity;
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
import com.receiptofi.web.form.ReceiptForm;
import com.receiptofi.web.helper.ReceiptLandingView;
import com.receiptofi.web.rest.JsonReceiptDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    @Value ("${ReceiptController.redirectAccessLandingController:redirect:/access/landing.htm}")
    private String redirectAccessLandingController;

    @Value ("${ReceiptController.nextPage:/receipt2}")
    private String nextPage;

    @Value ("${ReceiptController.nextPageByBiz:/receiptByBiz}")
    private String nextPageByBiz;

    @Autowired private ReceiptService receiptService;
    @Autowired private ItemService itemService;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

    @RequestMapping (value = "/{receiptId}", method = RequestMethod.GET)
    public ModelAndView loadForm(
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
            List<ExpenseTagEntity> expenseTypes = userProfilePreferenceService.activeExpenseTypes(receiptUser.getRid());

            receiptForm.setReceipt(receipt);
            receiptForm.setItems(items);
            receiptForm.setExpenseTags(expenseTypes);
            LOG.debug("receiptForm={}", receiptForm);
        }
        return new ModelAndView(nextPage);
    }

    @RequestMapping (value = "/rest/{receiptId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonReceiptDetail loadReceipt(
            @PathVariable
            String receiptId
    ) {
        JsonReceiptDetail jsonReceiptDetail = new JsonReceiptDetail();
        LOG.info("Loading Receipt Item with id={}", receiptId);
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId, receiptUser.getRid());
        if (null == receiptEntity) {
            LOG.warn("User={}, tried submitting an invalid receipt={}", receiptUser.getRid(), receiptId);
        } else {
            List<ItemEntity> items = itemService.getAllItemsOfReceipt(receiptEntity.getId());
            List<ExpenseTagEntity> expenseTypes = userProfilePreferenceService.activeExpenseTypes(receiptUser.getRid());

            jsonReceiptDetail.setJsonReceipt(new JsonReceipt(receiptEntity));

            List<JsonReceiptItem> jsonReceiptItems = new LinkedList<>();
            for (ItemEntity itemEntity : items) {
                JsonReceiptItem jsonReceiptItem = JsonReceiptItem.newInstance(itemEntity);
                jsonReceiptItems.add(jsonReceiptItem);
            }
            jsonReceiptDetail.setItems(jsonReceiptItems);

            List<JsonExpenseTag> jsonExpenseTags = new ArrayList<>();
            for (ExpenseTagEntity expenseTagEntity : expenseTypes) {
                jsonExpenseTags.add(JsonExpenseTag.newInstance(expenseTagEntity));
            }
            jsonReceiptDetail.setJsonExpenseTags(jsonExpenseTags);


            LOG.info("populate receipt json for id={}", receiptId);
        }
        return jsonReceiptDetail;
    }

    @RequestMapping (method = RequestMethod.POST, params = "delete")
    public String delete(@ModelAttribute ("receiptForm") ReceiptForm receiptForm) {
        LOG.info("Delete receipt rid={}", receiptForm.getReceipt().getId());
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            receiptService.deleteReceipt(receiptForm.getReceipt().getId(), receiptUser.getRid());
            //TODO(hth) in case of failure to delete send message to USER
        } catch (Exception e) {
            LOG.error("Error occurred during receipt delete: Receipt={}, reason={}",
                    receiptForm.getReceipt().getId(),
                    e.getLocalizedMessage(),
                    e);
        }
        return redirectAccessLandingController;
    }

    @RequestMapping (method = RequestMethod.POST, params = "re-check")
    public ModelAndView recheck(@ModelAttribute ("receiptForm") ReceiptForm receiptForm) {
        LOG.info("Initiating re-check on receipt rid={}", receiptForm.getReceipt().getId());

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            receiptService.reopen(receiptForm.getReceipt().getId(), receiptUser.getRid());
        } catch (Exception exce) {
            LOG.error("Receipt={} reason={}", receiptForm.getReceipt().getId(), exce.getLocalizedMessage(), exce);

            receiptForm.setErrorMessage(exce.getLocalizedMessage());
            return loadForm(receiptForm.getReceipt().getId(), receiptForm);
        }
        return new ModelAndView(redirectAccessLandingController);
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping (value = "/biz/{id}", method = RequestMethod.GET)
    public ModelAndView receiptByBizName(@PathVariable String id) throws IOException {
        LOG.info("Loading Receipts by Biz Name id={}", id);

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ReceiptLandingView> receiptLandingViews = new ArrayList<>();

        ModelAndView modelAndView = new ModelAndView(nextPageByBiz);

        List<BizNameEntity> bizNames = bizNameManager.findAllBizWithMatchingName(id);
        for (BizNameEntity bizNameEntity : bizNames) {
            List<ReceiptEntity> receipts = receiptService.findReceipt(bizNameEntity, receiptUser.getRid());
            for (ReceiptEntity receiptEntity : receipts) {
                receiptLandingViews.add(ReceiptLandingView.newInstance(receiptEntity));
            }
        }

        modelAndView.addObject("receiptLandingViews", receiptLandingViews);
        return modelAndView;
    }
}
