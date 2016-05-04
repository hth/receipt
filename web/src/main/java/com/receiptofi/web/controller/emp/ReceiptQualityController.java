package com.receiptofi.web.controller.emp;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.web.form.ReceiptQualityForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * User: hitender
 * Date: 5/2/16 9:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp")
public class ReceiptQualityController {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptQualityController.class);

    @Value ("${nextPage:/emp/receiptQuality}")
    private String nextPage;

    private ReceiptService receiptService;
    private ItemService itemService;

    @Autowired
    public ReceiptQualityController(ReceiptService receiptService, ItemService itemService) {
        this.receiptService = receiptService;
        this.itemService = itemService;
    }

    @PreAuthorize ("hasAnyRole('ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/receiptQuality",
            method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("receiptQualityForm")
            ReceiptQualityForm receiptQualityForm
    ) {
        List<ReceiptEntity> receipts = receiptService.getReceiptsWithoutQC();
        for (ReceiptEntity receipt : receipts) {
            receiptQualityForm.setReceiptAndItems(receipt, itemService.getAllItemsOfReceipt(receipt.getId()));
        }
        return nextPage;
    }
}
