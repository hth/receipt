package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
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

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @Value ("${registration.invite.daily.limit}")
    private int registrationInviteDailyLimit;

    @Autowired private AccountService accountService;
    @Autowired private MailService mailService;
    @Autowired private BillingService billingService;
    @Autowired private CronStatsService cronStatsService;

    @Scheduled (cron = "${loader.SendOutAccountActiveEmail.registrationCompleteEmail}")
    public void registrationCompleteEmail() {
        LOG.info("begins");

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
                        billingAccount.setCreateAndUpdate(new Date());
                        billingService.save(billingAccount);

                        BillingHistoryEntity billingHistory = billingService.findBillingHistoryForMonth(
                                new Date(),
                                billingAccount.getRid());

                        if (billingHistory == null) {
                            /**
                             * Mark PROMOTIONAL as billed for the first and second month.
                             * First month marked PROMOTIONAL during signup.
                             */
                            billingHistory = new BillingHistoryEntity(
                                    userAccount.getReceiptUserId(),
                                    new Date());
                            billingHistory.setBilledStatus(BilledStatusEnum.P);
                            billingHistory.setBillingPlan(BillingPlanEnum.P);
                            billingService.save(billingHistory);
                        } else {
                            billingHistory.setBilledStatus(BilledStatusEnum.P);
                            billingHistory.setBillingPlan(BillingPlanEnum.P);
                            billingService.save(billingHistory);
                        }

                        billingHistory = billingService.findBillingHistoryForMonth(
                                Date.from(LocalDateTime.now().plusMonths(1).toInstant(ZoneOffset.UTC)),
                                billingAccount.getRid());

                        if (billingHistory == null) {
                            /**
                             * Second month marked as PROMOTIONAL too. Second month Bill History can exists as it
                             * would be created by billing cron task. Even if it exists this will over ride to
                             * PROMOTIONAL status for that month.
                             */
                            billingHistory = new BillingHistoryEntity(
                                    userAccount.getReceiptUserId(),
                                    Date.from(LocalDateTime.now().plusMonths(1).toInstant(ZoneOffset.UTC)));
                            billingHistory.setBilledStatus(BilledStatusEnum.P);
                            billingHistory.setBillingPlan(BillingPlanEnum.P);
                            billingService.save(billingHistory);
                        } else {
                            billingHistory.setBilledStatus(BilledStatusEnum.P);
                            billingHistory.setBillingPlan(BillingPlanEnum.P);
                            billingService.save(billingHistory);
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
