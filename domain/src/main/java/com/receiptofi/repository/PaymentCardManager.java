package com.receiptofi.repository;

import com.receiptofi.domain.PaymentCardEntity;
import com.receiptofi.domain.annotation.Mobile;

import java.util.List;

/**
 * User: hitender
 * Date: 8/30/16 6:27 PM
 */
public interface PaymentCardManager extends RepositoryManager<PaymentCardEntity> {

    PaymentCardEntity findCard(String rid, String cardDigit);

    @Mobile
    PaymentCardEntity findOne(String id, String rid);

    List<PaymentCardEntity> getPaymentCards(String rid);
}
