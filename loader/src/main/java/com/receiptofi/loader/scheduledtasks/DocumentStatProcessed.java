package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.DocumentDailyStatEntity;
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
 * User: hitender
 * Date: 11/20/14 12:47 PM
 */
@Component
public class DocumentStatProcessed {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentStatProcessed.class);
    private static final int A_DAY = 1;

    @Value ("${statStartDate:2013-01-01T00:00:00Z}")
    private String statStartDate;

    @Value ("${generateDocumentStat:ON}")
    private String generateDocumentStat;

    @Autowired private DocumentDailyStatService dailyStatService;

    /**
     * Note: For every two second use *\/2 * * * * ? where as cron string blow run every day at 12:00 AM.
     */
    @Scheduled (cron = "0 0 0 * * ?")
    public void computeDocumentDailyStat() {
        if ("ON".equalsIgnoreCase(generateDocumentStat)) {
            LOG.info("feature is {}", generateDocumentStat);
            DocumentDailyStatEntity lastEntry = dailyStatService.getLastEntry();
            if (lastEntry == null) {
                LOG.warn("initializing DocumentDailyStatEntity");
                initialized();
                lastEntry = dailyStatService.getLastEntry();
            }

            Assert.notNull(lastEntry);

            if (Days.daysBetween(new DateTime(lastEntry.getDate()), DateUtil.midnight(DateTime.now())).getDays() > A_DAY) {
                Date computeSince = new DateTime(lastEntry.getDate()).plusDays(1).toDate();
                Map<Date, DocumentDailyStatEntity> dailyStat = dailyStatService.computeDailyStats(computeSince);
                for (Date day : dailyStat.keySet()) {
                    dailyStatService.save(dailyStat.get(day));
                }
            }
        } else {
            LOG.info("feature is {}", generateDocumentStat);
        }
    }

    /**
     * When no record exists, initialize data with specified start date.
     */
    private void initialized() {
        dailyStatService.save(new DocumentDailyStatEntity(DateUtil.midnight(new DateTime(statStartDate)).toDate()));
    }
}
