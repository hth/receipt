package com.receiptofi.loader.service;

import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.braintreegateway.WebhookNotification;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.PaymentGatewayEnum;
import com.receiptofi.service.BillingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * User: hitender
 * Date: 6/10/15 10:54 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class SubscriptionService {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired private BillingService billingService;

    public void processSubscription(WebhookNotification notification) {
        Subscription subscription = notification.getSubscription();
        Assert.hasText(subscription.getId(), "SubscriptionId is empty");

        Transaction transaction = subscription.getTransactions().get(0);
        Assert.hasText(transaction.getId(), "Transaction is empty");

        BillingAccountEntity billingAccount = billingService.getBySubscription(subscription.getId(), PaymentGatewayEnum.BT);
        LOG.info("subscriptionId={} transactionId={} rid={}", subscription.getId(), transaction.getId(), billingAccount.getRid());

        BillingHistoryEntity billingHistory;

        switch (notification.getKind()) {
            case SUBSCRIPTION_CANCELED:
                billingAccount.setAccountBillingType(AccountBillingTypeEnum.NB);
                billingService.save(billingAccount);
                break;
            case SUBSCRIPTION_CHARGED_SUCCESSFULLY:
                billingHistory = billingService.findBillingHistoryForMonth(new Date(), billingAccount.getRid());
                //And check for transactionId too or if BillingHistory is active
                /** This can happen when sign up and subscription are on the same day. */
                if (billingHistory.getBilledStatus() == BilledStatusEnum.B && billingHistory.isActive()) {
                    LOG.info("Found existing history with billed status. Creating another history.");
                    billingHistory = new BillingHistoryEntity(billingAccount.getRid(), new Date());
                    billingHistory.setBilledStatus(BilledStatusEnum.B);
                    AccountBillingTypeEnum accountBillingType = AccountBillingTypeEnum.valueOf(subscription.getPlanId());
                    billingHistory.setAccountBillingType(accountBillingType);
                    billingHistory.setPaymentGateway(PaymentGatewayEnum.BT);
                    billingHistory.setTransactionId(transaction.getId());
                } else {
                    billingHistory.setBilledStatus(BilledStatusEnum.B);
                    billingHistory.setTransactionId(transaction.getId());
                }

                LOG.info("Saved Billing History");
                billingService.save(billingHistory);
                break;
            case SUBSCRIPTION_CHARGED_UNSUCCESSFULLY:
                billingHistory = billingService.findBillingHistoryForMonth(new Date(), billingAccount.getRid());
                billingHistory.setBilledStatus(BilledStatusEnum.NB);
                billingHistory.setTransactionId(transaction.getId());
                billingService.save(billingHistory);
                break;
            case SUBSCRIPTION_EXPIRED:
                break;
            case SUBSCRIPTION_TRIAL_ENDED:
                break;
            case SUBSCRIPTION_WENT_ACTIVE:
                /** Subscription when active only after first successful transaction. */
                break;
            case SUBSCRIPTION_WENT_PAST_DUE:
                billingHistory = billingService.findBillingHistoryForMonth(new Date(), billingAccount.getRid());
                billingHistory.setBilledStatus(BilledStatusEnum.NB);
                billingHistory.setTransactionId(transaction.getId());
                billingService.save(billingHistory);
                break;
            default:
                LOG.error("WebhookNotification kind={} not defined {}", notification.getKind(), notification);
                throw new UnsupportedOperationException("WebhookNotification kind not defined" + notification.getKind());
        }
    }
}