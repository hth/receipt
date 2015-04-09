package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.service.NotificationService;

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

/**
 * User: hitender
 * Date: 4/8/15 10:28 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class NotificationPurgeProcess {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsPurgeProcess.class);

    private NotificationService notificationService;

    private int inactiveNotificationAfterDay;
    private int purgeNotificationAfterDay;
    private String purgeNotification;

    @Autowired
    public NotificationPurgeProcess(
            @Value ("${purgeNotificationAfterDay:90}")
            int purgeNotificationAfterDay,

            @Value ("${inactiveNotificationAfterDay:45}")
            int inactiveNotificationAfterDay,

            @Value ("${purgeNotification:ON}")
            String purgeNotification,

            NotificationService notificationService
    ) {
        this.purgeNotificationAfterDay = purgeNotificationAfterDay;
        this.inactiveNotificationAfterDay = inactiveNotificationAfterDay;
        this.purgeNotification = purgeNotification;
        this.notificationService = notificationService;
    }

    @Scheduled (cron = "${loader.NotificationPurgeProcess.purgeNotificationDocument}")
    public void purgeNotificationDocument() {
        LOG.info("begins");
        if ("ON".equalsIgnoreCase(purgeNotification)) {
            int deletedCount = 0, inactiveCount = 0;
            try {
                Instant since = LocalDateTime.now().minusDays(purgeNotificationAfterDay).toInstant(ZoneOffset.UTC);
                Date sinceDate = Date.from(since);
                deletedCount = notificationService.deleteInactiveNotification(sinceDate);

                since = LocalDateTime.now().minusDays(inactiveNotificationAfterDay).toInstant(ZoneOffset.UTC);
                sinceDate = Date.from(since);
                inactiveCount = notificationService.setNotificationInactive(sinceDate);
            } catch (Exception e) {
                LOG.error("Error during notification purge or marking inactive, reason={}", e.getLocalizedMessage(), e);
            } finally {
                LOG.info("Notification deletedCount={} inactiveCount={}", deletedCount, inactiveCount);
            }
        } else {
            LOG.info("feature is {}", purgeNotification);
        }
    }
}
