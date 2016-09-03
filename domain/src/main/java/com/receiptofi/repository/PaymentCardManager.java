package com.receiptofi.repository;

import com.receiptofi.domain.PaymentCardEntity;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 8/30/16 6:27 PM
 */
public interface PaymentCardManager extends RepositoryManager<PaymentCardEntity> {
    void updateLastUsed(String rid, String cardDigit, Date lastUsed);

    /**
     * Used when receipt is deleted.
     *
     * @param rid
     * @param cardDigit
     */
    void decreaseUsed(String rid, String cardDigit);

    void increaseUsed(String rid, String cardDigit);

    PaymentCardEntity findCard(String rid, String cardDigit);

    List<PaymentCardEntity> getPaymentCards(String rid);
}
