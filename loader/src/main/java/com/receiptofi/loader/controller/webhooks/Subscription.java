package com.receiptofi.loader.controller.webhooks;

import com.braintreegateway.WebhookNotification;

import com.receiptofi.loader.service.PaymentGatewayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping (value = "/open/webhooks/subscription")
public class Subscription {
    private static final Logger LOG = LoggerFactory.getLogger(Subscription.class);

    @Autowired private PaymentGatewayService paymentGatewayService;

    @RequestMapping (method = RequestMethod.GET)
    public String getSubscription(@RequestParam String bt_challenge) {
        LOG.info("Subscription called");
        return paymentGatewayService.getGateway().webhookNotification().verify(bt_challenge);
    }

    @RequestMapping (method = RequestMethod.POST)
    public String postSubscription(
            @RequestParam String bt_signature,
            @RequestParam String bt_payload
    ) {
        LOG.info("Subscription post called");
        WebhookNotification webhookNotification = paymentGatewayService.getGateway().webhookNotification().parse(
                bt_signature,
                bt_payload
        );
        LOG.info("[Webhook Received " + webhookNotification.getTimestamp().getTime() + "] | " +
                "Kind: " + webhookNotification.getKind() + " | " +
                "Subscription: " + webhookNotification.getSubscription().getId());


        switch (webhookNotification.getKind()) {
            case SUBSCRIPTION_CANCELED:
                webhookNotification.getTimestamp();
                webhookNotification.getSubscription().getId();
                break;
            case SUBSCRIPTION_CHARGED_SUCCESSFULLY:
                break;
            case SUBSCRIPTION_CHARGED_UNSUCCESSFULLY:
                break;
            case SUBSCRIPTION_EXPIRED:
                break;
            case SUBSCRIPTION_TRIAL_ENDED:
                break;
            case SUBSCRIPTION_WENT_ACTIVE:
                break;
            case SUBSCRIPTION_WENT_PAST_DUE:
                break;
            default:
                LOG.error("WebhookNotification kind={} not defined {}", webhookNotification.getKind(), webhookNotification);
                throw new UnsupportedOperationException("WebhookNotification kind not defined" + webhookNotification.getKind());
        }
        return("");
    }
}
