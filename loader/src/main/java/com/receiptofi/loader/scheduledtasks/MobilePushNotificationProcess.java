package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.loader.service.MobilePushNotificationService;
import com.receiptofi.repository.NotificationManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: hitender
 * Date: 8/30/15 1:59 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class MobilePushNotificationProcess {
    private static final Logger LOG = LoggerFactory.getLogger(MobilePushNotificationProcess.class);

    private String notifyUserSwitch;
    private int documentLastUpdated;
    private MobilePushNotificationService mobilePushNotificationService;
    private DocumentService documentService;
    private AccountService accountService;
    private CronStatsService cronStatsService;
    private NotificationManager notificationManager;

    @Autowired
    public MobilePushNotificationProcess(
            @Value ("${MobilePushNotificationProcess.notifyUserSwitch}")
            String notifyUserSwitch,

            @Value ("${MobilePushNotificationProcess.how.long.ago.document.updated}")
            int documentLastUpdated,

            MobilePushNotificationService mobilePushNotificationService,
            DocumentService documentService,
            AccountService accountService,
            CronStatsService cronStatsService,
            NotificationManager notificationManager

    ) {
        this.notifyUserSwitch = notifyUserSwitch;
        this.documentLastUpdated = documentLastUpdated;
        this.mobilePushNotificationService = mobilePushNotificationService;
        this.documentService = documentService;
        this.accountService = accountService;
        this.cronStatsService = cronStatsService;
        this.notificationManager = notificationManager;
    }

    /**
     * Note: Cron string below runs every 5 minutes.
     */
    @Scheduled (cron = "${loader.MobilePushNotificationProcess.documentNotification}")
    public void documentNotification() {
        CronStatsEntity cronStats = new CronStatsEntity(
                MobilePushNotificationProcess.class.getName(),
                "GCM_Document_Notify",
                notifyUserSwitch);

        if ("OFF".equalsIgnoreCase(notifyUserSwitch)) {
            LOG.info("feature is {}", notifyUserSwitch);
            return;
        }

        List<UserAccountEntity> userAccounts;
        List<DocumentEntity> documents = documentService.getDocumentsForNotification(documentLastUpdated);
        if (!documents.isEmpty()) {
            userAccounts = accountService.findAllTechnician();
            LOG.info("Notification for received new document upload to be sent, document count={}", documents.size());
        } else {
            /** No notification on documents to be sent. */
            return;
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (DocumentEntity document : documents) {
                try {
                    documentService.markNotified(document.getId());
                    switch (document.getDocumentStatus()) {
                        case PENDING:
                            LOG.info("Notifying technicians on received new documents={} documentId={} rid={}",
                                    document.getDocumentStatus(),
                                    document.getId(),
                                    document.getReceiptUserId());

                            for (UserAccountEntity userAccount : userAccounts) {
                                mobilePushNotificationService.sendNotification(
                                        "New document received.",
                                        userAccount.getReceiptUserId());
                            }
                            success++;
                            break;
                        case REPROCESS:
                            LOG.info("Notifying technicians on received new documents={} documentId={} rid={}",
                                    document.getDocumentStatus(),
                                    document.getId(),
                                    document.getReceiptUserId());

                            for (UserAccountEntity userAccount : userAccounts) {
                                mobilePushNotificationService.sendNotification(
                                        "Re-check document received.",
                                        userAccount.getReceiptUserId());
                            }
                            success++;
                            break;
                        case PROCESSED:
                        case REJECT:
                        case DUPLICATE:
                            skipped++;
                            break;
                        default:
                            LOG.error("DocumentStatus not defined {}", document.getDocumentStatus());
                            throw new UnsupportedOperationException("DocumentStatus not defined " + document.getDocumentStatus());
                    }
                } catch (Exception e) {
                    LOG.error("Received new document notification failure document={} reason={}",
                            document,
                            e.getLocalizedMessage(),
                            e);

                    failure++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending received new document notification reason={}",
                    e.getLocalizedMessage(),
                    e);

        } finally {
            cronStats.addStats("success", success);
            cronStats.addStats("skipped", skipped);
            cronStats.addStats("failure", failure);
            cronStats.addStats("found", documents.size());
            cronStatsService.save(cronStats);

            LOG.info("Received new document success={} skipped={} failure={} total={}",
                    success,
                    skipped,
                    failure,
                    documents.size());
        }
    }

    /**
     * Note: Cron string below runs every 1 minute.
     */
    @Scheduled (cron = "${loader.MobilePushNotificationProcess.notification}")
    public void notification() {
        CronStatsEntity cronStats = new CronStatsEntity(
                MobilePushNotificationProcess.class.getName(),
                "GCM_Notify",
                notifyUserSwitch);

        if ("OFF".equalsIgnoreCase(notifyUserSwitch)) {
            LOG.info("feature is {}", notifyUserSwitch);
            return;
        }

        List<NotificationEntity> notifications = notificationManager.getAllPushNotifications();
        if (notifications.isEmpty()) {
            /** No notification to be sent. */
            return;
        }

        int success = 0, failure = 0;
        try {
            for (NotificationEntity notification : notifications) {
                try {
                    if (mobilePushNotificationService.sendNotification(
                            notification.getMessage(),
                            notification.getReceiptUserId())) {

                        notification.setNotificationStateToSuccess();
                        success++;
                    } else {
                        LOG.warn("Push failure rid={} id={} message={}",
                                notification.getReceiptUserId(),
                                notification.getId(),
                                notification.getMessage());

                        notification.setNotificationStateToFailure();
                        failure++;
                    }

                    /** Increase count when success or failure. */
                    notification.addCount();
                    LOG.info("Push count={} rid={} id={}",
                            notification.getCount(),
                            notification.getReceiptUserId(),
                            notification.getId());

                    notificationManager.save(notification);
                } catch (Exception e) {
                    LOG.error("Notification failure notification={} reason={}",
                            notification,
                            e.getLocalizedMessage(),
                            e);

                    failure++;
                    notification.addCount();
                    notificationManager.save(notification);
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending notification reason={}",
                    e.getLocalizedMessage(),
                    e);

        } finally {
            cronStats.addStats("success", success);
            cronStats.addStats("failure", failure);
            cronStats.addStats("found", notifications.size());
            cronStatsService.save(cronStats);

            LOG.info("Push Notification success={} failure={} total={}",
                    success,
                    failure,
                    notifications.size());
        }
    }
}
