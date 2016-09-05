package com.receiptofi.web.controller.emp.receipt;

import com.receiptofi.domain.PaymentCardEntity;
import com.receiptofi.domain.json.JsonPaymentCard;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.PaymentCardService;
import com.receiptofi.utils.ScrubbedInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 8/30/16 7:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp/receipt/pc")
public class PaymentCardController {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentCardController.class);

    private PaymentCardService paymentCardService;

    @Autowired
    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @RequestMapping (
            value = "/{rid}",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "getPaymentCards", keyGenerator = "customKeyGenerator")
    public List<JsonPaymentCard> getPaymentCards(
            @PathVariable
            ScrubbedInput rid
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("payment cards for rid={} by emp={}", rid.getText(), receiptUser.getRid());

        List<PaymentCardEntity> cards = paymentCardService.getPaymentCards(rid.getText());
        return cards.stream().map(JsonPaymentCard::new).collect(Collectors.toList());
    }

    @RequestMapping (
            value = "/d/{rid}",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "getPaymentCardDigits", keyGenerator = "customKeyGenerator")
    @ResponseBody
    public List<String> getPaymentCardDigits(
            @PathVariable
            ScrubbedInput rid
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("payment card digits for rid={} by emp={}", rid.getText(), receiptUser.getRid());

        return paymentCardService.getPaymentCardDigits(rid.getText());
    }
}
