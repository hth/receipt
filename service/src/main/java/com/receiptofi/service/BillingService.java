package com.receiptofi.service;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.repository.BillingAccountManager;
import com.receiptofi.repository.BillingHistoryManager;
import com.receiptofi.repository.UserAccountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
            List<BillingAccountEntity> billingAccounts = billingAccountManager.getAllBillingAccount(rid);
            billingAccounts.forEach(billingAccountManager::deleteHard);

            List<BillingHistoryEntity> billings = billingHistoryManager.getHistory(rid);
            billings.forEach(billingHistoryManager::deleteHard);

            if (billings.size() > 1 || billingAccounts.size() > 1) {
                LOG.error("Deleting billing history of size={} and billing account of size={} for rid={}",
                        billings.size(),
                        billingAccounts.size(),
                        rid);
            } else if (!billings.isEmpty() || !billingAccounts.isEmpty()) {
                LOG.info("Deleted billing history and account for rid={}", rid);
            }
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
        BillingHistoryEntity billingHistory = findBillingHistoryForMonth(receipt.getReceiptDate(), receipt.getReceiptUserId());
        if (null != billingHistory) {
            receipt.setBilledStatus(billingHistory.getBilledStatus());
        } else {
            UserAccountEntity userAccount = userAccountManager.findByReceiptUserId(receipt.getReceiptUserId());
            billingHistory = new BillingHistoryEntity(receipt.getReceiptUserId(), receipt.getReceiptDate());

            if (receipt.getReceiptDate().before(userAccount.getBillingAccount().getCreated())) {
                /** Mark all receipts before account creation as PROMOTIONAL. */
                LOG.warn("Create billing history rid={} yearMonth={}",
                        receipt.getReceiptUserId(),
                        BillingHistoryEntity.SDF.format(receipt.getReceiptDate()));

                billingHistory.setBilledStatus(BilledStatusEnum.P);
                billingHistory.setAccountBillingType(AccountBillingTypeEnum.P);
            }

            billingHistoryManager.save(billingHistory);
            receipt.setBilledStatus(billingHistory.getBilledStatus());
        }
    }

    public BillingHistoryEntity findBillingHistoryForMonth(Date date, String rid) {
        return billingHistoryManager.findBillingHistoryForMonth(BillingHistoryEntity.SDF.format(date), rid);
    }

    public List<BillingHistoryEntity> getHistory(String rid) {
        return billingHistoryManager.getHistory(rid);
    }

    public BillingAccountEntity getBillingAccount(String rid) {
        return billingAccountManager.getBillingAccount(rid);
    }

    public long countLastPromotion(Date date, String rid) {
        return billingHistoryManager.countLastPromotion(date, rid);
    }
}
