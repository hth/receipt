package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BillingService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2/18/15 2:14 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class SendOutAccountActiveEmail {
    private static final Logger LOG = LoggerFactory.getLogger(SendOutAccountActiveEmail.class);

    private int promotionalPeriod;
    private boolean registrationTurnedOn;
    private int registrationInviteDailyLimit;

    private AccountService accountService;
    private MailService mailService;
    private BillingService billingService;
    private CronStatsService cronStatsService;

    @Autowired
    public SendOutAccountActiveEmail(
            @Value ("${promotionalPeriod}")
            int promotionalPeriod,

            @Value ("${registration.turned.on}")
            boolean registrationTurnedOn,

            @Value ("${registration.invite.daily.limit}")
            int registrationInviteDailyLimit,

            AccountService accountService,
            MailService mailService,
            BillingService billingService,
            CronStatsService cronStatsService
    ) {
        this.promotionalPeriod = promotionalPeriod;
        this.registrationTurnedOn = registrationTurnedOn;
        this.registrationInviteDailyLimit = registrationInviteDailyLimit;
        this.accountService = accountService;
        this.mailService = mailService;
        this.billingService = billingService;
        this.cronStatsService = cronStatsService;

    }

    @Scheduled (cron = "${loader.SendOutAccountActiveEmail.registrationCompleteEmail}")
    public void registrationCompleteEmail() {
        LOG.info("begins");
        Date now = new Date();

        CronStatsEntity cronStats = new CronStatsEntity(
                SendOutAccountActiveEmail.class.getName(),
                "Registration_Complete_Email",
                registrationTurnedOn ? "ON" : "OFF");

        if (registrationTurnedOn) {
            List<UserAccountEntity> userAccounts = accountService.findRegisteredAccountWhenRegistrationIsOff(registrationInviteDailyLimit);

            int success = 0, failure = 0, skipped = 0;
            for (UserAccountEntity userAccount : userAccounts) {
                if (userAccount.isRegisteredWhenRegistrationIsOff()) {
                    if (mailService.registrationCompleteEmail(userAccount.getUserId(), userAccount.getName())) {

                        /** Reset new account create date as this is the time onwards PROMOTIONAL is going to be active. */
                        BillingAccountEntity billingAccount = userAccount.getBillingAccount();
                        billingAccount.setCreateAndUpdate(now);
                        billingService.save(billingAccount);

                        for (int monthCount = 0; monthCount < promotionalPeriod; monthCount++) {
                            BillingHistoryEntity billingHistory = billingService.findLatestBillingHistoryForMonth(
                                    Date.from(LocalDateTime.now().plusMonths(monthCount).toInstant(ZoneOffset.UTC)),
                                    billingAccount.getRid());

                            if (billingHistory == null) {
                                /**
                                 * Mark PROMOTIONAL as billed for the number of month count.
                                 * Number of months marked PROMOTIONAL during signup.
                                 */
                                billingHistory = new BillingHistoryEntity(
                                        userAccount.getReceiptUserId(),
                                        Date.from(LocalDateTime.now().plusMonths(monthCount).toInstant(ZoneOffset.UTC)));
                                billingHistory.setBilledStatus(BilledStatusEnum.P);
                                billingHistory.setBillingPlan(BillingPlanEnum.P);
                                billingService.save(billingHistory);
                            } else {
                                billingHistory.setBilledStatus(BilledStatusEnum.P);
                                billingHistory.setBillingPlan(BillingPlanEnum.P);
                                billingService.save(billingHistory);
                            }
                        }

                        success++;
                    } else {
                        failure++;
                    }
                } else {
                    skipped++;
                }

                accountService.removeRegistrationIsOffFrom(userAccount.getId());
            }

            cronStats.addStats("total", userAccounts.size());
            cronStats.addStats("success", success);
            cronStats.addStats("skipped", skipped);
            cronStats.addStats("failure", failure);
            cronStatsService.save(cronStats);

            LOG.info("Registration complete mail sent total={} success={} skipped={}  failure={}",
                    userAccounts.size(), success, skipped, failure);
        } else {
            LOG.info("registrationTurnedOn feature is {}", registrationTurnedOn);
        }
    }
}
