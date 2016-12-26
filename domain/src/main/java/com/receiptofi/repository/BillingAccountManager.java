package com.receiptofi.repository;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.types.PaymentGatewayEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 3/19/15 2:51 PM
 */
public interface BillingAccountManager extends RepositoryManager<BillingAccountEntity> {
    BillingAccountEntity getBillingAccount(String rid);

    List<BillingAccountEntity> getAllBillingAccount(String rid);

    BillingAccountEntity getBySubscription(String subscriptionId, PaymentGatewayEnum paymentGatewayEnum);

    List<BillingAccountEntity> getAllBilling();
}
