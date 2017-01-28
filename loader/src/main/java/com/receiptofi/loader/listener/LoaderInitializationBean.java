package com.receiptofi.loader.listener;

import com.receiptofi.loader.scheduledtasks.BillingProcess;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BillingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * To make sure process runs just once on a server put it on loader. This was written when there was intermittent
 * failure in creating new account as returned value for new userGeneratedId was sometimes from secondary which
 * had stale id. Instead this was fixed by added option to query for returning generated id. This should fade away
 * when things start working perfect. In any case this should not be done as cleanup process should be managed by
 * the ones that fail. A separate task is big no no.
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
    private AccountService accountService;

    @Value ("${LoaderInitializationBean.cleanupOperation.switch:OFF}")
    private String cleanupOperation;

    @Value ("${LoaderInitializationBean.createPlaceholderForBilling.switch:OFF}")
    private String createPlaceholderForBilling;

    @Autowired
    public LoaderInitializationBean(
            BillingProcess billingProcess,
            BillingService billingService,
            AccountService accountService) {
        LOG.info("Initialized Loader");
        this.billingProcess = billingProcess;
        this.billingService = billingService;
        this.accountService = accountService;
    }

    /**
     * This needs to run when there are mis-match.
     *
     * BILLING_ACCOUNT when account fails to create the reminiscence of the failure is not deleted.
     * BILLING_HISTORY when account fails to create the reminiscence of the failure is not deleted and shows as duplicate.
     * USER_ACCOUNT is the actual number of accounts.
     * USER_AUTHENTICATION when account fails to create the reminiscence of the failure is not deleted.
     * USER_PREFERENCE when account fails to create, user preference fails to be created.
     * USER_PROFILE is always created when account is failed. This helps replace the failed USER_ACCOUNT creation.
     *
     * This should never be run and instead these issue needs to be fixed.
     */
    @PostConstruct
    public void cleanupOperation() {
        if("ON".equalsIgnoreCase(cleanupOperation)) {
            LOG.info("........ Cleanup operation is {} ........ ", cleanupOperation);
            removeBillingAccountOrphan();
            removeAuthenticationOrphan();
            createMissingUserPreferences();
        } else {
            LOG.info("Cleanup operation is {}", cleanupOperation);
        }
    }

    private void removeBillingAccountOrphan() {
        billingService.removeOrphanBillingAccount();
        if ("ON".equalsIgnoreCase(createPlaceholderForBilling)) {
            billingProcess.createPlaceholderForBilling();
        }
        accountService.removeDuplicatesBillingHistory();
    }

    private void removeAuthenticationOrphan() {
        accountService.removeAuthenticationOrphan();
    }

    private void createMissingUserPreferences() {
        accountService.removeUserPreferencesOrphan();
        accountService.createMissingUserPreferences();
        accountService.createMissingExpenseTags();
    }
}
