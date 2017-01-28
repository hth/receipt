package com.receiptofi.loader.listener;

import com.receiptofi.loader.scheduledtasks.BillingProcess;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BillingService;
import com.receiptofi.service.ExpensesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private AccountService accountService;
    private ExpensesService expensesService;

    @Value ("${LoaderInitializationBean.cleanupOperation.switch:ON}")
    private String cleanupOperation;

    @Autowired
    public LoaderInitializationBean(
            BillingProcess billingProcess,
            BillingService billingService,
            AccountService accountService,
            ExpensesService expensesService) {
        LOG.info("Initialized Loader");
        this.billingProcess = billingProcess;
        this.billingService = billingService;
        this.accountService = accountService;
        this.expensesService = expensesService;
    }

    /**
     * This needs to run when there are mis-match.
     *
     * BILLING_ACCOUNT when account fails to create the reminiscence of the failure is not deleted.
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
        //billingProcess.createPlaceholderForBilling();
    }

    private void removeAuthenticationOrphan() {
        accountService.removeAuthenticationOrphan();
    }

    private void createMissingUserPreferences() {
        accountService.removeUserPreferencesOrphan();
        accountService.createMissingUserPreferences();
        accountService.createMissingExpenseTags();
        accountService.removeDuplicatesBillingHistory();
    }
}
