package com.receiptofi.loader.listener;

import com.receiptofi.loader.scheduledtasks.BillingProcess;
import com.receiptofi.service.BillingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * To make sure process runs just once on a server put it on loader.
 *
 * User: hitender
 * Date: 12/26/16 5:42 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class LoaderInitializationBean {

    private static final Logger LOG = LoggerFactory.getLogger(LoaderInitializationBean.class);

    private BillingProcess billingProcess;
    private BillingService billingService;

    @Autowired
    public LoaderInitializationBean(BillingProcess billingProcess, BillingService billingService) {
        LOG.info("Initialized Loader");
        this.billingProcess = billingProcess;
        this.billingService = billingService;
    }

    @PostConstruct
    public void removeBillingAccountOrphan() {
        billingService.removeOrphanBillingAccount();
        //billingProcess.createPlaceholderForBilling();
    }
}
