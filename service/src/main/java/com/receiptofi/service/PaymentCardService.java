package com.receiptofi.service;

import com.receiptofi.domain.PaymentCardEntity;
import com.receiptofi.repository.PaymentCardManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 8/30/16 6:51 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class PaymentCardService {

    private PaymentCardManager paymentCardManager;

    @Autowired
    public PaymentCardService(PaymentCardManager paymentCardManager) {
        this.paymentCardManager = paymentCardManager;
    }

    public void save(PaymentCardEntity paymentCard) {
        paymentCardManager.save(paymentCard);
    }

    public PaymentCardEntity findCard(String rid, String cardDigit) {
        return paymentCardManager.findCard(rid, cardDigit);
    }

    public List<String> getPaymentCardDigits(String rid) {
        List<PaymentCardEntity> paymentCards = paymentCardManager.getActivePaymentCards(rid);
        return paymentCards.stream().map(PaymentCardEntity::getCardDigit).collect(Collectors.toList());
    }
}
