package com.receiptofi.web.controller.emp.receipt;

import com.receiptofi.domain.CreditCardEntity;
import com.receiptofi.domain.json.JsonCreditCard;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.CreditCardService;
import com.receiptofi.utils.ScrubbedInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping (value = "/emp/cc")
public class CreditCardController {
    private static final Logger LOG = LoggerFactory.getLogger(CreditCardController.class);

    private CreditCardService creditCardService;

    @Autowired
    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @RequestMapping (
            value = "/{rid}",
            method = RequestMethod.GET,
            headers = "Accept=application/json",
            produces = "application/json")
    @Cacheable (value = "getCreditCards", keyGenerator = "customKeyGenerator")
    public List<JsonCreditCard> getCreditCards(
            @PathVariable
            ScrubbedInput rid
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("credit cards for rid={} by emp={}", rid.getText(), receiptUser.getRid());

        List<CreditCardEntity> cards = creditCardService.getCreditCards(rid.getText());
        return cards.stream().map(JsonCreditCard::new).collect(Collectors.toList());
    }
}
