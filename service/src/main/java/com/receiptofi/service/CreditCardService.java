package com.receiptofi.service;

import com.receiptofi.domain.CreditCardEntity;
import com.receiptofi.repository.CreditCardManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
public class CreditCardService {

    private CreditCardManager creditCardManager;

    @Autowired
    public CreditCardService(CreditCardManager creditCardManager) {
        this.creditCardManager = creditCardManager;
    }

    public void save(CreditCardEntity creditCard) {
        creditCardManager.save(creditCard);
    }

    void updateLastUsed(String rid, String cardDigit, Date lastUsed) {
        creditCardManager.updateLastUsed(rid, cardDigit, lastUsed);
    }

    void increaseUsed(String rid, String cardDigit) {
        creditCardManager.increaseUsed(rid, cardDigit);
    }

    void decreaseUsed(String rid, String cardDigit) {
        creditCardManager.decreaseUsed(rid, cardDigit);
    }

    public CreditCardEntity findCard(String rid, String cardDigit) {
        return creditCardManager.findCard(rid, cardDigit);
    }

    public List<CreditCardEntity> getCreditCards(String rid) {
        return creditCardManager.getCreditCards(rid);
    }
}
