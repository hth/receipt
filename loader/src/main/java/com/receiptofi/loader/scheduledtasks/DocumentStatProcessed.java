package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentDailyStatEntity;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentDailyStatService;
import com.receiptofi.utils.DateUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;

/**
 * Daily document processed stats.
 * User: hitender
 * Date: 11/20/14 12:47 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class DocumentStatProcessed {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentStatProcessed.class);
    private static final int A_DAY = 1;

    private String statStartDate;
    private String generateDocumentStat;
    private DocumentDailyStatService dailyStatService;
    private CronStatsService cronStatsService;

    @Autowired
    public DocumentStatProcessed(
            @Value ("${statStartDate:2013-01-01T00:00:00Z}")
            String statStartDate,

            @Value ("${generateDocumentStat:ON}")
            String generateDocumentStat,

            DocumentDailyStatService dailyStatService,
            CronStatsService cronStatsService
    ) {
        this.statStartDate = statStartDate;
        this.generateDocumentStat = generateDocumentStat;
        this.dailyStatService = dailyStatService;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (cron = "${loader.DocumentStatProcessed.computeDocumentDailyStat}")
    public void computeDocumentDailyStat() {
        CronStatsEntity cronStats = new CronStatsEntity(
                DocumentStatProcessed.class.getName(),
                "Compute_Document_Daily_Stat",
                generateDocumentStat);

        if ("ON".equalsIgnoreCase(generateDocumentStat)) {
            LOG.info("feature is {}", generateDocumentStat);

            int success = 0, failure = 0, skipped = 0, found = 0;
            try {
                DocumentDailyStatEntity lastEntry = dailyStatService.getLastEntry();
                if (null == lastEntry) {
                    LOG.warn("initializing DocumentDailyStatEntity");
                    initialized();
                    lastEntry = dailyStatService.getLastEntry();
                }

                Assert.notNull(lastEntry);

                int days = Days.daysBetween(new DateTime(lastEntry.getDate()), DateUtil.midnight(DateTime.now())).getDays();
                LOG.info("last stat computed for document processed on date={} was {} days ago", lastEntry.getDate(), days);
                if (days > A_DAY) {
                    Date computeSince = new DateTime(lastEntry.getDate()).plusDays(1).toDate();
                    Map<Date, DocumentDailyStatEntity> dailyStat = dailyStatService.computeDailyStats(computeSince);
                    found = dailyStat.size();

                    for (Date day : dailyStat.keySet()) {
                        dailyStatService.save(dailyStat.get(day));
                        success++;
                    }
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                LOG.error("Error computing daily document stats, reason={}", e.getLocalizedMessage(), e);
                failure++;
            } finally {
                cronStats.addStats("found", found);
                cronStats.addStats("success", success);
                cronStats.addStats("skipped", skipped);
                cronStats.addStats("failure", failure);
                cronStatsService.save(cronStats);
            }
        } else {
            LOG.info("feature is {}", generateDocumentStat);
        }
    }

    /**
     * When no record exists, initialize data with specified start date.
     */
    private void initialized() {
        LOG.warn("since no record exists, initializing database");
        dailyStatService.save(new DocumentDailyStatEntity(DateUtil.midnight(new DateTime(statStartDate)).toDate()));
    }
}
