package com.receiptofi.loader.scheduledtasks;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.NotificationEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.loader.service.MobilePushNotificationService;
import com.receiptofi.repository.NotificationManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.utils.DateUtil;

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
    private int notificationRetryCount;
    private MobilePushNotificationService mobilePushNotificationService;
    private DocumentUpdateService documentUpdateService;
    private StorageManager storageManager;
    private ReceiptManager receiptManager;
    private AccountService accountService;
    private CronStatsService cronStatsService;
    private NotificationManager notificationManager;

    @Autowired
    public MobilePushNotificationProcess(
            @Value ("${MobilePushNotificationProcess.notifyUserSwitch}")
            String notifyUserSwitch,

            @Value ("${MobilePushNotificationProcess.notification_retry_count:5}")
            int notificationRetryCount,

            MobilePushNotificationService mobilePushNotificationService,
            DocumentUpdateService documentUpdateService,
            StorageManager storageManager,
            ReceiptManager receiptManager,
            AccountService accountService,
            CronStatsService cronStatsService,
            NotificationManager notificationManager

    ) {
        this.notifyUserSwitch = notifyUserSwitch;
        this.notificationRetryCount = notificationRetryCount;
        this.mobilePushNotificationService = mobilePushNotificationService;
        this.documentUpdateService = documentUpdateService;
        this.storageManager = storageManager;
        this.receiptManager = receiptManager;
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

        List<UserAccountEntity> userAccountEntities;
        List<DocumentEntity> documents = documentUpdateService.getDocumentsForNotification(5);
        if (!documents.isEmpty()) {
            userAccountEntities = accountService.findAllTechnician();
            LOG.info("Notification to be send, count={}", documents.size());
        } else {
            /** No notification on documents to be sent. */
            return;
        }

        ReceiptEntity receipt;
        int success = 0, failure = 0, skipped = 0;
        try {
            for (DocumentEntity document : documents) {
                try {
                    documentUpdateService.markNotified(document.getId());
                    switch (document.getDocumentStatus()) {
                        case PENDING:
                            LOG.info("Notifying technicians on documents={} documentId={} rid={}",
                                    document.getDocumentStatus(), document.getId(), document.getReceiptUserId());

                            for (UserAccountEntity userAccount : userAccountEntities) {
                                mobilePushNotificationService.sendNotification(
                                        "New document received.",
                                        userAccount.getReceiptUserId());
                            }
                            success++;
                            break;
                        case PROCESSED:
                            receipt = receiptManager.findReceipt(document.getReferenceDocumentId(), document.getReceiptUserId());
                            mobilePushNotificationService.sendNotification(
                                    documentUpdateService.getNotificationMessageForReceiptProcess(receipt),
                                    document.getReceiptUserId());
                            success++;
                            break;
                        case REPROCESS:
                            LOG.info("Notifying technicians on documents={} documentId={} rid={}",
                                    document.getDocumentStatus(), document.getId(), document.getReceiptUserId());

                            for (UserAccountEntity userAccount : userAccountEntities) {
                                mobilePushNotificationService.sendNotification(
                                        "Re-check document received.",
                                        userAccount.getReceiptUserId());
                            }
                            success++;
                            break;
                        case REJECT:
                            GridFSDBFile gridFSDBFile = storageManager.get(document.getFileSystemEntities().iterator().next().getBlobId());
                            DBObject dbObject = gridFSDBFile.getMetaData();
                            mobilePushNotificationService.sendNotification(
                                    documentUpdateService.getNotificationMessageForReceiptReject(dbObject, document.getDocumentRejectReason()),
                                    document.getReceiptUserId());
                            success++;
                            break;
                        case DUPLICATE:
                            skipped++;
                            break;
                        default:
                            LOG.error("DocumentStatus not defined {}", document.getDocumentStatus());
                            throw new UnsupportedOperationException("DocumentStatus not defined " + document.getDocumentStatus());
                    }
                } catch (Exception e) {
                    LOG.error("Notification failure document={} reason={}", document, e.getLocalizedMessage(), e);
                    failure++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending document notification reason={}", e.getLocalizedMessage(), e);
        } finally {
            cronStats.addStats("success", success);
            cronStats.addStats("skipped", skipped);
            cronStats.addStats("failure", failure);
            cronStats.addStats("found", documents.size());
            cronStatsService.save(cronStats);

            LOG.info("Documents upload success={} skipped={} failure={} total={}", success, skipped, failure, documents.size());
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

        List<NotificationEntity> notificationEntities = notificationManager.getAllPushNotifications(DateUtil.getDateMinusMinutes(1));
        if (notificationEntities.isEmpty()) {
            /** No notification to be sent. */
            return;
        }

        int success = 0, failure = 0;
        try {
            for (NotificationEntity notification : notificationEntities) {
                try {
                    if (mobilePushNotificationService.sendNotification(
                            notification.getMessage(),
                            notification.getReceiptUserId())) {
                        notification.markAsNotified();
                        success++;
                    } else {
                        notification.addCount();
                        if (notification.getCount() >= notificationRetryCount) {
                            notification.inActive();
                        }
                        failure++;
                    }
                    notificationManager.save(notification);
                } catch (Exception e) {
                    LOG.error("Notification failure notification={} reason={}", notification, e.getLocalizedMessage(), e);

                    failure++;
                    notification.addCount();
                    notificationManager.save(notification);
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending notification reason={}", e.getLocalizedMessage(), e);
        } finally {
            cronStats.addStats("success", success);
            cronStats.addStats("failure", failure);
            cronStats.addStats("found", notificationEntities.size());
            cronStatsService.save(cronStats);

            LOG.info("Push Notification success={} failure={} total={}", success, failure, notificationEntities.size());
        }
    }
}
