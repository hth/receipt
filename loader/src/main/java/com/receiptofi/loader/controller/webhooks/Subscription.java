package com.receiptofi.loader.controller.webhooks;

import com.braintreegateway.WebhookNotification;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.PaymentGatewayEnum;
import com.receiptofi.loader.service.PaymentGatewayService;
import com.receiptofi.service.BillingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * User: hitender
 * Date: 6/1/15 11:12 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/webhooks/subscription")
public class Subscription {
    private static final Logger LOG = LoggerFactory.getLogger(Subscription.class);

    @Autowired private PaymentGatewayService paymentGatewayService;
    @Autowired private BillingService billingService;

    @RequestMapping (method = RequestMethod.GET)
    @ResponseBody
    public String getSubscription(@RequestParam String bt_challenge) {
        LOG.info("Subscription called with bt_challenge");
        return paymentGatewayService.getGateway().webhookNotification().verify(bt_challenge);
    }

    @RequestMapping (method = RequestMethod.POST)
    @ResponseBody
    public String postSubscription(
            @RequestParam String bt_signature,
            @RequestParam String bt_payload
    ) {
        LOG.debug("Subscription post called");
        WebhookNotification notification = paymentGatewayService.getGateway().webhookNotification().parse(
                bt_signature,
                bt_payload
        );

        LOG.info("Webhook time={} kind={} subscription={}",
                notification.getTimestamp().getTime(),
                notification.getKind(),
                notification.getSubscription().getId());

        String subscriptionId = notification.getSubscription().getId();
        Assert.hasText(subscriptionId, "SubscriptionId is empty");
        BillingAccountEntity billingAccount = billingService.getBySubscription(subscriptionId, PaymentGatewayEnum.BT);
        BillingHistoryEntity billingHistory;

        switch (notification.getKind()) {
            case SUBSCRIPTION_CANCELED:
                billingAccount.setAccountBillingType(AccountBillingTypeEnum.NB);
                billingService.save(billingAccount);
                break;
            case SUBSCRIPTION_CHARGED_SUCCESSFULLY:
                billingHistory = billingService.findBillingHistoryForMonth(new Date(), billingAccount.getRid());
                billingHistory.setBilledStatus(BilledStatusEnum.B);
                billingHistory.setTransactionId(notification.getTransaction().getId());
                billingService.save(billingHistory);
                break;
            case SUBSCRIPTION_CHARGED_UNSUCCESSFULLY:
                billingHistory = billingService.findBillingHistoryForMonth(new Date(), billingAccount.getRid());
                billingHistory.setBilledStatus(BilledStatusEnum.NB);
                billingHistory.setTransactionId(notification.getTransaction().getId());
                billingService.save(billingHistory);
                break;
            case SUBSCRIPTION_EXPIRED:
                LOG.error("Subscription={} subscription={} rid={}",
                        notification.getKind(), notification.getSubscription().getId(), billingAccount.getRid());
                break;
            case SUBSCRIPTION_TRIAL_ENDED:
                LOG.error("Subscription={} subscription={} rid={}",
                        notification.getKind(), notification.getSubscription().getId(), billingAccount.getRid());
                break;
            case SUBSCRIPTION_WENT_ACTIVE:
                /** Subscription when active only after first successful transaction. */
                LOG.info("Subscription={} subscription={} rid={}",
                        notification.getKind(), notification.getSubscription().getId(), billingAccount.getRid());
                break;
            case SUBSCRIPTION_WENT_PAST_DUE:
                billingHistory = billingService.findBillingHistoryForMonth(new Date(), billingAccount.getRid());
                billingHistory.setBilledStatus(BilledStatusEnum.NB);
                billingHistory.setTransactionId(notification.getTransaction().getId());
                billingService.save(billingHistory);
                break;
            default:
                LOG.error("WebhookNotification kind={} not defined {}", notification.getKind(), notification);
                throw new UnsupportedOperationException("WebhookNotification kind not defined" + notification.getKind());
        }
        return "";
    }
}
