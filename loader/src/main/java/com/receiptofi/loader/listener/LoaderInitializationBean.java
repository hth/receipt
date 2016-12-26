package com.receiptofi.loader.listener;

import com.receiptofi.loader.scheduledtasks.BillingProcess;
import com.receiptofi.service.BillingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

/**
 * To make sure process run just once on a server put it on loader.
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

    @Value ("${fileserver.ftp.host}")
    private String host;

    private BillingProcess billingProcess;
    private BillingService billingService;

    @Autowired
    public LoaderInitializationBean(BillingProcess billingProcess, BillingService billingService) {
        LOG.info("Initialized Loader");
        this.billingProcess = billingProcess;
        this.billingService = billingService;
    }

    @PostConstruct
    public void checkBillingAccountOrphan() {
        try {
            String hostname = InetAddress.getLocalHost().getHostAddress();
            LOG.info("hostname={}", hostname);

            if (hostname.equalsIgnoreCase(host)) {
                billingService.removeOrphanBillingAccount();
                billingProcess.createPlaceholderForBilling();
            }
        } catch (UnknownHostException e) {
            LOG.error("failed to get hostname={}", e.getLocalizedMessage(), e.getLocalizedMessage());
        }
    }
}
