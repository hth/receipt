package com.receiptofi.service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 6/2/15 5:40 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class PaymentGatewayService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

    private BraintreeGateway gateway;

    @Autowired
    public PaymentGatewayService(
            @Value ("${braintree.environment}")
            String brainTreeEnvironment,

            @Value ("${braintree.merchant_id}")
            String brainTreeMerchantId,

            @Value ("${braintree.public_key}")
            String brainTreePublicKey,

            @Value ("${braintree.private_key}")
            String brainTreePrivateKey
    ) {
        if ("PRODUCTION".equals(brainTreeEnvironment)) {
            gateway = new BraintreeGateway(
                    Environment.PRODUCTION,
                    brainTreeMerchantId,
                    brainTreePublicKey,
                    brainTreePrivateKey
            );
            LOG.info("{} gateway initialized", brainTreeEnvironment);
        } else {
            gateway = new BraintreeGateway(
                    Environment.SANDBOX,
                    brainTreeMerchantId,
                    brainTreePublicKey,
                    brainTreePrivateKey
            );
            LOG.info("{} gateway initialized", brainTreeEnvironment);
        }
    }

    public BraintreeGateway getGateway() {
        return gateway;
    }
}
