package com.receiptofi.loader.scheduledtasks;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.loader.service.GoogleCloudMessagingService;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.repository.StorageManager;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentUpdateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
public class GoogleCloudMessagingProcess {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleCloudMessagingProcess.class);

    private String notifyUserSwitch;
    private GoogleCloudMessagingService googleCloudMessagingService;
    private DocumentUpdateService documentUpdateService;
    private StorageManager storageManager;
    private ReceiptManager receiptManager;
    private AccountService accountService;
    private CronStatsService cronStatsService;

    @Autowired
    public GoogleCloudMessagingProcess(
            @Value ("${GoogleCloudMessagingProcess.notifyUserSwitch}")
            String notifyUserSwitch,

            GoogleCloudMessagingService googleCloudMessagingService,
            DocumentUpdateService documentUpdateService,
            StorageManager storageManager,
            ReceiptManager receiptManager,
            AccountService accountService,
            CronStatsService cronStatsService

    ) {
        this.notifyUserSwitch = notifyUserSwitch;
        this.googleCloudMessagingService = googleCloudMessagingService;
        this.documentUpdateService = documentUpdateService;
        this.storageManager = storageManager;
        this.receiptManager = receiptManager;
        this.accountService = accountService;
        this.cronStatsService = cronStatsService;
    }

    /**
     * Note: Cron string blow run every 5 minutes.
     */
    @Scheduled (cron = "${loader.GoogleCloudMessagingProcess.notification}")
    public void notification() {
        CronStatsEntity cronStats = new CronStatsEntity(
                GoogleCloudMessagingProcess.class.getName(),
                "GCM_Notify",
                notifyUserSwitch);

        if ("OFF".equalsIgnoreCase(notifyUserSwitch)) {
            LOG.info("feature is {}", notifyUserSwitch);
            return;
        }

        List<UserAccountEntity> userAccountEntities = new ArrayList<>();
        List<DocumentEntity> documents = documentUpdateService.getDocumentsForNotification(5);
        if (!documents.isEmpty()) {
            userAccountEntities = accountService.findAllTechnician();
            LOG.info("Notification to be send, count={}", documents.size());
        }

        ReceiptEntity receipt;
        int success = 0, failure = 0, skipped = 0;
        for (DocumentEntity document : documents) {
            try {
                documentUpdateService.markNotified(document.getId());
                switch (document.getDocumentStatus()) {
                    case PENDING:
                        LOG.info("Notifying technicians on documents={} documentId={} rid={}",
                                document.getDocumentStatus(), document.getId(), document.getReceiptUserId());

                        for (UserAccountEntity userAccount : userAccountEntities) {
                            googleCloudMessagingService.sendNotification(
                                    "New document received.",
                                    userAccount.getReceiptUserId());
                        }
                        success++;
                        break;
                    case PROCESSED:
                        receipt = receiptManager.findReceipt(document.getReferenceDocumentId(), document.getReceiptUserId());
                        googleCloudMessagingService.sendNotification(
                                documentUpdateService.getNotificationMessageForReceiptProcess(receipt),
                                document.getReceiptUserId());
                        success++;
                        break;
                    case REPROCESS:
                        LOG.info("Notifying technicians on documents={} documentId={} rid={}",
                                document.getDocumentStatus(), document.getId(), document.getReceiptUserId());

                        for (UserAccountEntity userAccount : userAccountEntities) {
                            googleCloudMessagingService.sendNotification(
                                    "Re-check document received.",
                                    userAccount.getReceiptUserId());
                        }
                        success++;
                        break;
                    case REJECT:
                        GridFSDBFile gridFSDBFile = storageManager.get(document.getFileSystemEntities().iterator().next().getBlobId());
                        DBObject dbObject = gridFSDBFile.getMetaData();
                        googleCloudMessagingService.sendNotification(
                                documentUpdateService.getNotificationMessageForReceiptReject(dbObject, document.getDocumentRejectReason()),
                                document.getReceiptUserId());
                        success++;
                        break;
                    case DUPLICATE:
                        skipped++;
                        break;
                    default:

                }
            } catch (Exception e) {
                LOG.error("Notification failure document={} reason={}", document, e.getLocalizedMessage(), e);
                failure++;
            } finally {
                cronStats.addStats("success", success);
                cronStats.addStats("skipped", skipped);
                cronStats.addStats("failure", failure);
                cronStats.addStats("found", documents.size());
                cronStatsService.save(cronStats);

                LOG.info("Documents upload success={} skipped={} failure={} total={}", success, skipped, failure, documents.size());
            }
        }
    }
}
