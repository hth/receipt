package com.receiptofi.service;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.repository.BillingAccountManager;
import com.receiptofi.repository.BillingHistoryManager;

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

    @Autowired private BillingAccountManager billingAccountManager;
    @Autowired private BillingHistoryManager billingHistoryManager;

    public void save(BillingAccountEntity billingAccount) {
        billingAccountManager.save(billingAccount);
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
