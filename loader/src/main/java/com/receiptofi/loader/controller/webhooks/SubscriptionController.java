package com.receiptofi.loader.controller.webhooks;

import com.braintreegateway.WebhookNotification;
import com.braintreegateway.exceptions.InvalidSignatureException;

import com.receiptofi.loader.service.PaymentGatewayService;
import com.receiptofi.loader.service.SubscriptionService;

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

    @Autowired private PaymentGatewayService paymentGatewayService;
    @Autowired private SubscriptionService subscriptionService;

    @RequestMapping (method = RequestMethod.GET)
    @ResponseBody
    public String getSubscription(
            @RequestParam (required = true) String bt_challenge
    ) {
        LOG.info("Subscription called with bt_challenge");
        return paymentGatewayService.getGateway().webhookNotification().verify(bt_challenge);
    }

    @RequestMapping (method = RequestMethod.POST)
    @ResponseBody
    public String postSubscription(
            @RequestParam (required = true) String bt_signature,
            @RequestParam (required = true) String bt_payload,
            HttpServletResponse httpServletResponse

    ) throws IOException {
        LOG.debug("Subscription post called");
        WebhookNotification notification;
        try {
            notification = paymentGatewayService.getGateway().webhookNotification().parse(
                    bt_signature,
                    bt_payload
            );
        } catch (InvalidSignatureException e) {
            LOG.error("Failed parsing payload reason={}", e.getLocalizedMessage(), e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            return null;
        }

        subscriptionService.processSubscription(notification);
        return "";
    }
}
