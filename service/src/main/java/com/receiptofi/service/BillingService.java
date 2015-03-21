package com.receiptofi.service;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.repository.BillingAccountManager;
import com.receiptofi.repository.BillingHistoryManager;
import com.receiptofi.repository.UserAccountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 3/19/15 4:42 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BillingService {
    private static final Logger LOG = LoggerFactory.getLogger(BillingService.class);

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private BillingAccountManager billingAccountManager;
    @Autowired private BillingHistoryManager billingHistoryManager;

    public void save(BillingAccountEntity billingAccount) {
        billingAccountManager.save(billingAccount);
    }

    /**
     * Only be called when account creation fails. No userAccount exists is the condition required to be satisfied
     * before invoking internals of this method.
     *
     * @param rid
     */
    public void deleteHardBillingWhenAccountCreationFails(String rid) {
        if (null == userAccountManager.findByReceiptUserId(rid)) {
            billingAccountManager.deleteHard(billingAccountManager.getBillingAccount(rid));
            List<BillingHistoryEntity> billings = billingHistoryManager.getHistory(rid);
            if (billings.size() > 1) {
                LOG.error("Deleting billing history greater than 1 rid={}", rid);
            }
            billings.forEach(billingHistoryManager::deleteHard);
        }
    }

    public void save(BillingHistoryEntity billingHistory) {
        billingHistoryManager.save(billingHistory);
    }

    /**
     * Update Receipt with billing information for the receipt transaction month.
     *
     * @param receipt
     */
    public void updateReceiptWithBillingHistory(ReceiptEntity receipt) {
        BillingHistoryEntity billingHistory = billingHistoryManager.findBillingHistoryForMonth(
                BillingHistoryEntity.SDF.format(receipt.getReceiptDate()),
                receipt.getReceiptUserId());

        if (null != billingHistory) {
            receipt.setBilledStatus(billingHistory.getBilledStatus());
        }
    }

    public List<BillingHistoryEntity> getHistory(String rid) {
        return billingHistoryManager.getHistory(rid);
    }
}
