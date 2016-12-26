package com.receiptofi.service;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.exceptions.NotFoundException;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.types.PaymentGatewayEnum;
import com.receiptofi.domain.types.TransactionStatusEnum;
import com.receiptofi.repository.BillingAccountManager;
import com.receiptofi.repository.BillingHistoryManager;
import com.receiptofi.repository.UserAccountManager;

import org.apache.commons.lang3.StringUtils;

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

    private UserAccountManager userAccountManager;
    private BillingAccountManager billingAccountManager;
    private BillingHistoryManager billingHistoryManager;
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    public BillingService(
            UserAccountManager userAccountManager,
            BillingAccountManager billingAccountManager,
            BillingHistoryManager billingHistoryManager,
            PaymentGatewayService paymentGatewayService) {
        this.userAccountManager = userAccountManager;
        this.billingAccountManager = billingAccountManager;
        this.billingHistoryManager = billingHistoryManager;
        this.paymentGatewayService = paymentGatewayService;
    }

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
        BillingHistoryEntity billingHistory = findLatestBillingHistoryForMonth(receipt.getReceiptDate(), receipt.getReceiptUserId());
        if (null != billingHistory) {
            receipt.setBilledStatus(billingHistory.getBilledStatus());
        } else {
            UserAccountEntity userAccount = userAccountManager.findByReceiptUserId(receipt.getReceiptUserId());
            billingHistory = new BillingHistoryEntity(receipt.getReceiptUserId(), receipt.getReceiptDate());

            if (receipt.getReceiptDate().before(userAccount.getBillingAccount().getCreated())) {
                /** Mark all receipts before account creation as PROMOTIONAL. */
                LOG.warn("Create billing history rid={} yearMonth={}",
                        receipt.getReceiptUserId(),
                        BillingHistoryEntity.YYYY_MM.format(receipt.getReceiptDate()));

                billingHistory.setBilledStatus(BilledStatusEnum.P);
                billingHistory.setBillingPlan(BillingPlanEnum.P);
            }

            billingHistoryManager.save(billingHistory);
            receipt.setBilledStatus(billingHistory.getBilledStatus());
        }
    }

    public BillingHistoryEntity findLatestBillingHistoryForMonth(Date date, String rid) {
        return billingHistoryManager.findLatestBillingHistoryForMonth(BillingHistoryEntity.YYYY_MM.format(date), rid);
    }

    public List<BillingHistoryEntity> getHistory(String rid) {
        return billingHistoryManager.getHistory(rid);
    }

    public BillingAccountEntity getBillingAccount(String rid) {
        return billingAccountManager.getBillingAccount(rid);
    }

    public BillingAccountEntity getBySubscription(String subscriptionId, PaymentGatewayEnum paymentGateway) {
        return billingAccountManager.getBySubscription(subscriptionId, paymentGateway);
    }

    public long countLastPromotion(Date date, String rid) {
        return billingHistoryManager.countLastPromotion(date, rid);
    }

    /**
     * Voids unsettled transaction.
     */
    @Mobile
    public TransactionStatusEnum voidTransaction(BillingHistoryEntity billing) {
        if (StringUtils.isNotBlank(billing.getTransactionId())) {
            try {
                Result<Transaction> result =
                        paymentGatewayService.getGateway().transaction().voidTransaction(billing.getTransactionId());

                if (result.isSuccess()) {
                    LOG.info("void success transactionId={} rid={} resultId={}",
                            billing.getTransactionId(), billing.getRid(), result.getTarget().getId());

                    return TransactionStatusEnum.V;
                } else {
                    LOG.warn("void failed transactionId={} rid={} reason={}, trying refund",
                            billing.getTransactionId(), billing.getRid(), result.getMessage());

                    return refundTransaction(billing);
                }
            } catch (NotFoundException e) {
                LOG.error("Could not find transactionId={} reason={}",
                        billing.getTransactionId(), e.getLocalizedMessage(), e);

                return null;
            }
        } else {
            LOG.error("TransactionId is empty rid={}", billing.getRid());
            return null;
        }
    }

    /**
     * Refunds transaction. All transactions are settled at 5:00 PM or 7:00 AM CDT.
     */
    @Mobile
    public TransactionStatusEnum refundTransaction(BillingHistoryEntity billing) {
        if (StringUtils.isNotBlank(billing.getTransactionId())) {
            try {
                Result<Transaction> result =
                        paymentGatewayService.getGateway().transaction().refund(billing.getTransactionId());

                if (result.isSuccess()) {
                    LOG.info("refund success transactionId={} rid={}",
                            billing.getTransactionId(), billing.getRid());

                    return TransactionStatusEnum.R;
                } else {
                    LOG.warn("refund failed transactionId={} rid={} reason={}",
                            billing.getTransactionId(), billing.getRid(), result.getMessage());
                }
                return null;
            } catch (NotFoundException e) {
                LOG.error("Could not find transactionId={} reason={}",
                        billing.getTransactionId(), e.getLocalizedMessage(), e);

                return null;
            }
        } else {
            LOG.error("TransactionId is empty rid={}", billing.getRid());
            return null;
        }
    }

    public void removeOrphanBillingAccount() {
        List<BillingAccountEntity> billingAccounts = billingAccountManager.getAllBilling();
        for (BillingAccountEntity billingAccount : billingAccounts) {
            UserAccountEntity userAccount = userAccountManager.findByBillingAccount(billingAccount.getRid(), billingAccount.getId());
            if (userAccount == null) {
                LOG.warn("Orphan billing account={} rid={}", billingAccount.getId(), billingAccount.getRid());
                billingAccountManager.deleteHard(billingAccount);
            }
        }
    }
}
