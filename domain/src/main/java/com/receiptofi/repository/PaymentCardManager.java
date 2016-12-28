package com.receiptofi.repository;

import com.receiptofi.domain.PaymentCardEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 8/30/16 6:27 PM
 */
public interface PaymentCardManager extends RepositoryManager<PaymentCardEntity> {

    PaymentCardEntity findCard(String rid, String cardDigit);

    List<PaymentCardEntity> getActivePaymentCards(String rid);
}
