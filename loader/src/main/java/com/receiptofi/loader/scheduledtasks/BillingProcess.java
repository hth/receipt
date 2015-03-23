package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BillingService;

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
    private static final Logger LOG = LoggerFactory.getLogger(DocumentStatProcessed.class);

    private int promotionalPeriod;
    private int limit;
    private String billingProcessStatus;
    private BillingService billingService;
    private AccountService accountService;

    @Autowired
    public BillingProcess(
            @Value ("${promotionalPeriod:2}")
            int promotionalPeriod,

            @Value ("${limit:2}")
            int limit,

            @Value ("${billingProcessStatus:ON}")
            String billingProcessStatus,

            BillingService billingService,
            AccountService accountService
    ) {
        this.promotionalPeriod = promotionalPeriod;
        this.limit = limit;
        this.billingProcessStatus = billingProcessStatus;
        this.billingService = billingService;
        this.accountService = accountService;
    }

    /**
     * This will make the account due and would help in compute the net amount due for the account.
     */
    @Scheduled (cron = "${loader.BillingProcess.monthly}")
    public void createPlaceholderForBilling() {
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

            Instant nextMonth = LocalDateTime.now().plusMonths(1).toInstant(ZoneOffset.UTC);
            Date billedForMonth = Date.from(nextMonth);

            try {
                while (true) {
                    List<UserAccountEntity> userAccounts = accountService.findAllForBilling(skipDocuments, limit);

                    if (userAccounts.isEmpty()) {
                        break;
                    } else {
                        totalCount += userAccounts.size();
                    }

                    for (UserAccountEntity userAccount : userAccounts) {
                        BillingAccountEntity billingAccount = userAccount.getBillingAccount();
                        if (billingAccount != null) {
                            switch (billingAccount.getAccountBillingType()) {
                                case NB:
                                    if (billingService.findBillingHistoryForMonth(billedForMonth, billingAccount.getRid()) == null) {
                                        insertBillingHistory(billedForMonth, BilledStatusEnum.NB, AccountBillingTypeEnum.NB, billingAccount.getRid());

                                        markBillingAccountAsBilled(billingAccount);
                                        noBillingCount++;
                                        successCount++;
                                    } else {
                                        skippedNoBillingCount++;
                                    }
                                    break;
                                case P:
                                    if (billingService.countLastPromotion(billedForMonth, billingAccount.getRid()) >= promotionalPeriod) {
                                        if (billingService.findBillingHistoryForMonth(billedForMonth, billingAccount.getRid()) == null) {
                                            insertBillingHistory(billedForMonth, BilledStatusEnum.NB, AccountBillingTypeEnum.NB, billingAccount.getRid());

                                            markBillingAccountAsBilled(billingAccount);
                                            billingAccount.setAccountBillingType(AccountBillingTypeEnum.NB);
                                            billingService.save(billingAccount);

                                            noBillingCount++;
                                            successCount++;
                                        } else {
                                            skippedNoBillingCount++;
                                        }
                                    } else if (billingService.findBillingHistoryForMonth(billedForMonth, billingAccount.getRid()) == null) {
                                        insertBillingHistory(billedForMonth, BilledStatusEnum.P, AccountBillingTypeEnum.P, billingAccount.getRid());

                                        markBillingAccountAsBilled(billingAccount);
                                        promotionCount++;
                                        successCount++;
                                    } else {
                                        skippedPromotionCount++;
                                    }
                                    break;
                                case M30:
                                    if (billingService.findBillingHistoryForMonth(billedForMonth, billingAccount.getRid()) == null) {
                                        insertBillingHistory(billedForMonth, BilledStatusEnum.NB, AccountBillingTypeEnum.M30, billingAccount.getRid());

                                        markBillingAccountAsBilled(billingAccount);
                                        monthlyCount++;
                                        successCount++;
                                    } else {
                                        skippedMonthlyCount++;
                                    }
                                    break;
                                case A:
                                    /** This would get executed on December of every year. */
                                    if (billingService.findBillingHistoryForMonth(billedForMonth, billingAccount.getRid()) == null) {
                                        for (int i = 1; i <= 12; i++) {
                                            insertBillingHistory(
                                                    Date.from(LocalDateTime.now().plusMonths(i).toInstant(ZoneOffset.UTC)),
                                                    BilledStatusEnum.NB,
                                                    AccountBillingTypeEnum.A,
                                                    billingAccount.getRid());
                                        }

                                        markBillingAccountAsBilled(billingAccount);
                                        annualCount++;
                                        successCount++;
                                    } else {
                                        skippedAnnualCount++;
                                    }
                                    break;
                                default:
                                    failureCount++;
                                    LOG.error("Reached unreachable condition rid={} billingAccountTypeEnum={} ",
                                            userAccount.getReceiptUserId(), billingAccount.getAccountBillingType());

                                    throw new RuntimeException("Reached unreachable condition");
                            }
                        } else {
                            //This condition would not happen in prod
                        }
                    }
                    skipDocuments += limit;
                }
            } catch (Exception e) {
                LOG.error("error during billing reason={}", e.getLocalizedMessage(), e);
            } finally {
                LOG.info("billedForMonth={} totalCount={} successCount={} failureCount={} " +
                                "noBillingCount={} skippedNoBillingCount={} " +
                                "promotionCount={} skippedPromotionCount={} " +
                                "monthlyCount={} skippedMonthlyCount={}, " +
                                "annualCount={} skippedAnnualCount={}"
                        ,
                        BillingHistoryEntity.SDF.format(billedForMonth), totalCount, successCount, failureCount,
                        noBillingCount, skippedNoBillingCount,
                        promotionCount, skippedPromotionCount,
                        monthlyCount, skippedMonthlyCount,
                        annualCount, skippedAnnualCount
                );
            }
        } else {
            LOG.info("feature is {}", billingProcessStatus);
        }
    }

    private void insertBillingHistory(Date billedForMonth, BilledStatusEnum bs, AccountBillingTypeEnum abt, String rid) {
        BillingHistoryEntity billingHistory = new BillingHistoryEntity(rid, billedForMonth);
        billingHistory.setBilledStatus(bs);
        billingHistory.setAccountBillingType(abt);
        billingService.save(billingHistory);
    }

    private void markBillingAccountAsBilled(BillingAccountEntity billingAccount) {
        if (!billingAccount.isBilledAccount()) {
            billingAccount.markAccountBilled();
            billingService.save(billingAccount);
        }
    }
}
