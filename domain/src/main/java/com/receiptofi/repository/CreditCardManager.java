package com.receiptofi.repository;

import com.receiptofi.domain.CreditCardEntity;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 8/30/16 6:27 PM
 */
public interface CreditCardManager extends RepositoryManager<CreditCardEntity> {
    void updateLastUsed(String rid, String cardDigit, Date lastUsed);

    /**
     * Used when receipt is deleted.
     *
     * @param rid
     * @param cardDigit
     */
    void decreaseUsed(String rid, String cardDigit);

    void increaseUsed(String rid, String cardDigit);

    CreditCardEntity findCard(String rid, String cardDigit);

    List<CreditCardEntity> getCreditCards(String rid);
}
