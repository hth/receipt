package com.receiptofi.web.controller.emp;

import com.receiptofi.service.ReceiptService;
import com.receiptofi.web.form.ReceiptQualityForm;
import com.receiptofi.web.form.UserSearchForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @Autowired private ReceiptService receiptService;

    @PreAuthorize ("hasAnyRole('ROLE_SUPERVISOR')")
    @RequestMapping (
            value = "/receiptQuality",
            method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("receiptQualityForm")
            ReceiptQualityForm receiptQualityForm
    ) {
        return nextPage;
    }
}
