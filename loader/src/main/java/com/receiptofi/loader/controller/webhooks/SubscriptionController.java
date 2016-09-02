package com.receiptofi.loader.controller.webhooks;

import com.braintreegateway.WebhookNotification;
import com.braintreegateway.exceptions.InvalidSignatureException;

import com.receiptofi.loader.service.SubscriptionService;
import com.receiptofi.service.PaymentGatewayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
@RequestMapping (value = "/webhooks/subscription")
public class SubscriptionController {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionController.class);

    private final PaymentGatewayService paymentGatewayService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, PaymentGatewayService paymentGatewayService) {
        this.subscriptionService = subscriptionService;
        this.paymentGatewayService = paymentGatewayService;
    }

    @RequestMapping (method = RequestMethod.GET)
    @ResponseBody
    public String getSubscription(
            @RequestParam (required = true)
            String bt_challenge
    ) {
        LOG.info("Subscription called with bt_challenge");
        return paymentGatewayService.getGateway().webhookNotification().verify(bt_challenge);
    }

    @RequestMapping (method = RequestMethod.POST)
    @ResponseBody
    public String postSubscription(
            @RequestParam (required = true)
            String bt_signature,

            @RequestParam (required = true)
            String bt_payload,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        WebhookNotification notification;
        try {
            notification = paymentGatewayService.getGateway().webhookNotification().parse(
                    bt_signature,
                    bt_payload
            );
        } catch (InvalidSignatureException e) {
            LOG.error("Failed parsing payload {}", e.getLocalizedMessage(), e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            return null;
        }

        try {
            LOG.info("Webhook kind={} subscription={} time={}",
                    notification.getKind(),
                    notification.getSubscription().getId(),
                    notification.getTimestamp().getTime());
            subscriptionService.processSubscription(notification);
            httpServletResponse.sendError(HttpServletResponse.SC_OK, "");
            return null;
        } catch (Exception e) {
            LOG.error("Failed subscription {}", e.getLocalizedMessage(), e);
            //httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            httpServletResponse.sendError(HttpServletResponse.SC_OK, "");
            return null;
        }
    }
}
