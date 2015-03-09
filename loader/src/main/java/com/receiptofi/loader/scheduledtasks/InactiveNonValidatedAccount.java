package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.service.AccountService;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 2/2/15 10:27 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class InactiveNonValidatedAccount {
    private static final Logger LOG = LoggerFactory.getLogger(InactiveNonValidatedAccount.class);

    @Value ("${mail.validation.timeout.period}")
    private int mailValidationTimeoutPeriod;

    @Value ("${inactiveNonValidatedAccount:ON}")
    private String inactiveNonValidatedAccount;

    private AccountService accountService;

    @Autowired
    public InactiveNonValidatedAccount(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Marks account inactive when account email address has not be validated.
     */
    @Scheduled (cron = "${loader.InactiveNonValidatedAccount.markAccountInactiveWhenNotValidated}")
    public void markAccountInactiveWhenNotValidated() {
        LOG.info("begins");
        if (inactiveNonValidatedAccount.equalsIgnoreCase("ON")) {
            DateTime pastActivationDate = DateTime.now().minusDays(mailValidationTimeoutPeriod);
            LOG.info("marking accounts inactive which are past activation date={}", pastActivationDate.toString());
            int count = accountService.inactiveNonValidatedAccount(pastActivationDate.toDate());
            LOG.info("marked total number of account inactive={}", count);
        } else {
            LOG.info("feature is {}", inactiveNonValidatedAccount);
        }
    }
}
