package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.CronStatsService;

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

    @Value ("${inactiveNonValidatedAccountSwitch:ON}")
    private String inactiveNonValidatedAccountSwitch;

    private AccountService accountService;
    private CronStatsService cronStatsService;

    @Autowired
    public InactiveNonValidatedAccount(
            AccountService accountService,
            CronStatsService cronStatsService
    ) {
        this.accountService = accountService;
        this.cronStatsService = cronStatsService;
    }

    /**
     * Marks account inactive when account email address has not be validated.
     */
    @Scheduled (cron = "${loader.InactiveNonValidatedAccount.markAccountInactiveWhenNotValidated}")
    public void markAccountInactiveWhenNotValidated() {
        CronStatsEntity cronStats = new CronStatsEntity(
                InactiveNonValidatedAccount.class.getName(),
                "Mark_Account_Inactive_When_Not_Validated",
                inactiveNonValidatedAccountSwitch);

        long count = 0;
        LOG.info("begins");
        if ("ON".equals(inactiveNonValidatedAccountSwitch)) {
            DateTime pastActivationDate = DateTime.now().minusDays(mailValidationTimeoutPeriod);
            LOG.info("marking accounts inactive which are past activation date={}", pastActivationDate.toString());
            count = accountService.inactiveNonValidatedAccount(pastActivationDate.toDate());
            LOG.info("marked total number of account markedInactive={}", count);
        } else {
            LOG.info("feature is {}", inactiveNonValidatedAccountSwitch);
        }

        cronStats.addStats("markedInactive", count);
        cronStatsService.save(cronStats);
    }
}
