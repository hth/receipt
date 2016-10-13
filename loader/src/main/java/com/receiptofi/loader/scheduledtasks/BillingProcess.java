package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BillingService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.ReceiptService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 3/21/15 11:22 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class BillingProcess {
    private static final Logger LOG = LoggerFactory.getLogger(BillingProcess.class);

    private int promotionalPeriod;
    private int limit;
    private String billingProcessStatus;

    private BillingService billingService;
    private AccountService accountService;
    private ReceiptService receiptService;
    private CronStatsService cronStatsService;

    private boolean keepPromotionRunning = true;

    @Autowired
    public BillingProcess(
            @Value ("${promotionalPeriod}")
            int promotionalPeriod,

            @Value ("${limit:100}")
            int limit,

            @Value ("${billingProcessStatus:ON}")
            String billingProcessStatus,

            BillingService billingService,
            AccountService accountService,
            ReceiptService receiptService,
            CronStatsService cronStatsService
    ) {
        this.promotionalPeriod = promotionalPeriod;
        this.limit = limit;
        this.billingProcessStatus = billingProcessStatus;
        this.billingService = billingService;
        this.accountService = accountService;
        this.receiptService = receiptService;
        this.cronStatsService = cronStatsService;
    }

    /**
     * This will make the account due and would help in compute the net amount due for the account.
     */
    @Scheduled (cron = "${loader.BillingProcess.monthly}")
    public void createPlaceholderForBilling() {
        CronStatsEntity cronStats = new CronStatsEntity(
                BillingProcess.class.getName(),
                "Create_Placeholder_For_Billing",
                billingProcessStatus);

        if ("ON".equalsIgnoreCase(billingProcessStatus)) {
            LOG.info("feature is {}", billingProcessStatus);
            int skipDocuments = 0,
                    noBillingCount = 0,
                    skippedNoBillingCount = 0,
                    promotionCount = 0,
                    skippedPromotionCount = 0,
                    monthlyCount = 0,
                    skippedMonthlyCount = 0,
                    annualCount = 0,
                    skippedAnnualCount = 0,
                    successCount = 0,
                    failureCount = 0,
                    totalCount = 0;

            Instant nextMonth = LocalDateTime.now().toInstant(ZoneOffset.UTC);
            Date billedForMonth = Date.from(nextMonth);

            try {
                while (true) {
                    List<UserAccountEntity> userAccounts = accountService.findAllForBilling(skipDocuments, limit);
                    LOG.info("Found accounts to be billed size={} limit={}", userAccounts.size(), limit);

                    if (userAccounts.isEmpty()) {
                        break;
                    } else {
                        totalCount += userAccounts.size();
                    }

                    for (UserAccountEntity userAccount : userAccounts) {
                        BillingAccountEntity billingAccount = userAccount.getBillingAccount();
                        if (billingAccount != null) {
                            switch (billingAccount.getBillingPlan()) {
                                case NB:
                                    if (doesDocumentExistsInBillingHistory(billedForMonth, billingAccount)) {
                                        insertBillingHistory(
                                                billedForMonth,
                                                BilledStatusEnum.NB,
                                                BillingPlanEnum.NB,
                                                billingAccount.getRid());

                                        noBillingCount++;
                                        successCount++;
                                    } else {
                                        skippedNoBillingCount++;
                                    }
                                    break;
                                case P:
                                    if (!keepPromotionRunning && billingService.countLastPromotion(billedForMonth, billingAccount.getRid()) >= promotionalPeriod) {
                                        if (doesDocumentExistsInBillingHistory(billedForMonth, billingAccount)) {
                                            /**
                                             * Since the account is passed promotional period, its reset to NB and
                                             * billing account is updated accordingly.
                                             */
                                            insertBillingHistory(
                                                    billedForMonth,
                                                    BilledStatusEnum.NB,
                                                    BillingPlanEnum.NB,
                                                    billingAccount.getRid());

                                            billingAccount.setBillingPlan(BillingPlanEnum.NB);
                                            billingService.save(billingAccount);

                                            noBillingCount++;
                                            successCount++;
                                        } else {
                                            skippedNoBillingCount++;
                                        }
                                    } else if (doesDocumentExistsInBillingHistory(billedForMonth, billingAccount)) {
                                        insertBillingHistory(
                                                billedForMonth,
                                                BilledStatusEnum.P,
                                                billingAccount.getBillingPlan(),
                                                billingAccount.getRid());

                                        promotionCount++;
                                        successCount++;
                                    } else {
                                        skippedPromotionCount++;
                                    }
                                    break;
                                case M10:
                                case M30:
                                case M50:
                                case M100:
                                    if (doesDocumentExistsInBillingHistory(billedForMonth, billingAccount)) {
                                        insertBillingHistory(
                                                billedForMonth,
                                                BilledStatusEnum.NB,
                                                billingAccount.getBillingPlan(),
                                                billingAccount.getRid());

                                        monthlyCount++;
                                        successCount++;
                                    } else {
                                        skippedMonthlyCount++;
                                    }
                                    break;
                                case A120:
                                case A360:
                                case A600:
                                case A1200:
                                    /** This would get executed on December of every year. */
                                    if (doesDocumentExistsInBillingHistory(billedForMonth, billingAccount)) {
                                        for (int i = 1; i <= 12; i++) {
                                            insertBillingHistory(
                                                    Date.from(LocalDateTime.now().plusMonths(i).toInstant(ZoneOffset.UTC)),
                                                    BilledStatusEnum.NB,
                                                    billingAccount.getBillingPlan(),
                                                    billingAccount.getRid());
                                        }

                                        annualCount++;
                                        successCount++;
                                    } else {
                                        skippedAnnualCount++;
                                    }
                                    break;
                                default:
                                    failureCount++;
                                    LOG.error("Reached unreachable condition for billing placeholder rid={} billingAccountTypeEnum={} ",
                                            userAccount.getReceiptUserId(), billingAccount.getBillingPlan());

                                    throw new RuntimeException("Reached unreachable condition for billing placeholder");
                            }
                        } else {
                            //This condition would not happen in prod. TODO remove me in future.
                            billingAccount = new BillingAccountEntity(userAccount.getReceiptUserId());
                            billingService.save(billingAccount);

                            userAccount.setBillingAccount(billingAccount);
                            accountService.saveUserAccount(userAccount);

                            /**
                             * Mark PROMOTIONAL as billed for the first and second month.
                             * First month marked PROMOTIONAL during signup.
                             */
                            BillingHistoryEntity billingHistory = new BillingHistoryEntity(
                                    userAccount.getReceiptUserId(),
                                    new Date());
                            billingHistory.setBilledStatus(BilledStatusEnum.P);
                            billingHistory.setBillingPlan(BillingPlanEnum.P);
                            billingService.save(billingHistory);

                            /** Second month marked as PROMOTIONAL too. */
                            billingHistory = new BillingHistoryEntity(
                                    userAccount.getReceiptUserId(),
                                    Date.from(LocalDateTime.now().plusMonths(1).toInstant(ZoneOffset.UTC)));
                            billingHistory.setBilledStatus(BilledStatusEnum.P);
                            billingHistory.setBillingPlan(BillingPlanEnum.P);
                            billingService.save(billingHistory);

                            List<ReceiptEntity> receipts = receiptService.findAllReceipts(userAccount.getReceiptUserId());
                            for (ReceiptEntity receipt : receipts) {
                                receipt.setBilledStatus(BilledStatusEnum.P);
                                receiptService.save(receipt);
                            }
                        }
                    }
                    skipDocuments += limit;
                }
            } catch (Exception e) {
                LOG.error("error during billing reason={}", e.getLocalizedMessage(), e);
            } finally {
                cronStats.addStats("billedForMonth", BillingHistoryEntity.YYYY_MM.format(billedForMonth));
                cronStats.addStats("totalCount", totalCount);
                cronStats.addStats("successCount", successCount);
                cronStats.addStats("failureCount", failureCount);
                cronStats.addStats("noBillingCount", noBillingCount);
                cronStats.addStats("skippedNoBillingCount", skippedNoBillingCount);
                cronStats.addStats("promotionCount", promotionCount);
                cronStats.addStats("skippedPromotionCount", skippedPromotionCount);
                cronStats.addStats("monthlyCount", monthlyCount);
                cronStats.addStats("skippedMonthlyCount", skippedMonthlyCount);
                cronStats.addStats("annualCount", annualCount);
                cronStats.addStats("skippedAnnualCount", skippedAnnualCount);
                cronStatsService.save(cronStats);

                LOG.info("billedForMonth={} totalCount={} successCount={} failureCount={} " +
                                "noBillingCount={} skippedNoBillingCount={} " +
                                "promotionCount={} skippedPromotionCount={} " +
                                "monthlyCount={} skippedMonthlyCount={}, " +
                                "annualCount={} skippedAnnualCount={}"
                        ,
                        BillingHistoryEntity.YYYY_MM.format(billedForMonth), totalCount, successCount, failureCount,
                        noBillingCount, skippedNoBillingCount,
                        promotionCount, skippedPromotionCount,
                        monthlyCount, skippedMonthlyCount,
                        annualCount, skippedAnnualCount
                );
            }
        } else {
            LOG.info("Feature is {}", billingProcessStatus);
        }
    }

    /**
     * Check if BillingHistory exists.
     *
     * @param billedForMonth
     * @param billingAccount
     * @return
     */
    private boolean doesDocumentExistsInBillingHistory(Date billedForMonth, BillingAccountEntity billingAccount) {
        return billingService.findLatestBillingHistoryForMonth(billedForMonth, billingAccount.getRid()) == null;
    }

    /**
     * Create new BillingHistory.
     *
     * @param billedForMonth
     * @param bs
     * @param bp
     * @param rid
     */
    private void insertBillingHistory(Date billedForMonth, BilledStatusEnum bs, BillingPlanEnum bp, String rid) {
        BillingHistoryEntity billingHistory = new BillingHistoryEntity(rid, billedForMonth);
        billingHistory.setBilledStatus(bs);
        billingHistory.setBillingPlan(bp);
        billingService.save(billingHistory);
    }
}
