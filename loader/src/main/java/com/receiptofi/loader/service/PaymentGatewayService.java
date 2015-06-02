package com.receiptofi.loader.service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;

import com.receiptofi.repository.BillingAccountManager;
import com.receiptofi.repository.BillingHistoryManager;

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
    private BillingAccountManager billingAccountManager;
    private BillingHistoryManager billingHistoryManager;

    private String merchantAccountId;

    @Autowired
    public PaymentGatewayService(
            @Value ("${braintree.environment}")
            String brainTreeEnvironment,

            @Value ("${braintree.merchant_id}")
            String brainTreeMerchantId,

            @Value ("${braintree.public_key}")
            String brainTreePublicKey,

            @Value ("${braintree.private_key}")
            String brainTreePrivateKey,

            @Value ("${braintree.merchant_account_id}")
            String merchantAccountId,

            @Value ("${plan.cache.minutes}")
            int planCacheMinutes,

            BillingAccountManager billingAccountManager,
            BillingHistoryManager billingHistoryManager
    ) {
        if ("PRODUCTION".equals(brainTreeEnvironment)) {
            gateway = new BraintreeGateway(
                    Environment.PRODUCTION,
                    brainTreeMerchantId,
                    brainTreePublicKey,
                    brainTreePrivateKey
            );
        } else {
            gateway = new BraintreeGateway(
                    Environment.SANDBOX,
                    brainTreeMerchantId,
                    brainTreePublicKey,
                    brainTreePrivateKey
            );
        }

        this.billingAccountManager = billingAccountManager;
        this.billingHistoryManager = billingHistoryManager;
        this.merchantAccountId = merchantAccountId;
    }

    public BraintreeGateway getGateway() {
        return gateway;
    }
}
