package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentService;
import com.receiptofi.service.MessageDocumentService;

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
 * Date: 4/14/15 2:15 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class MessageDocumentOrphanReport {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDocumentOrphanReport.class);

    private String messageDocumentOrphanReportSwitch;
    private int pendingSinceDays;
    private MessageDocumentService messageDocumentService;
    private DocumentService documentService;
    private CronStatsService cronStatsService;

    @Autowired
    public MessageDocumentOrphanReport(
            @Value ("${messageDocumentOrphanReportSwitch:ON}")
            String messageDocumentOrphanReportSwitch,

            @Value ("${pendingSinceDays:2}")
            int pendingSinceDays,

            MessageDocumentService messageDocumentService,
            DocumentService documentService,
            CronStatsService cronStatsService
    ) {
        this.messageDocumentOrphanReportSwitch = messageDocumentOrphanReportSwitch;
        this.pendingSinceDays = pendingSinceDays;
        this.messageDocumentService = messageDocumentService;
        this.documentService = documentService;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (cron = "${loader.MessageDocumentOrphanReport.orphanMessageDocument}")
    public void orphanMessageDocument() {
        CronStatsEntity cronStats = new CronStatsEntity(
                MessageDocumentOrphanReport.class.getName(),
                "Message_Document_Orphan_Report",
                messageDocumentOrphanReportSwitch);

        if ("ON".equals(messageDocumentOrphanReportSwitch)) {
            Instant since = LocalDateTime.now().minusDays(pendingSinceDays).toInstant(ZoneOffset.UTC);
            Date sinceDate = Date.from(since);
            List<MessageDocumentEntity> pendingDocuments = messageDocumentService.findAllPending(sinceDate);

            int count = pendingDocuments.size(), success = 0, failure = 0, skipped = 0;
            try {
                for (MessageDocumentEntity messageDocument : pendingDocuments) {
                    DocumentEntity document = documentService.loadActiveDocumentById(messageDocument.getDocumentId());
                    if (null == document) {
                        LOG.warn("Orphan Message DocumentId={} messageDocumentId={}",
                                messageDocument.getDocumentId(), messageDocument.getId());
                        int deleted = messageDocumentService.deleteAllForReceiptOCR(messageDocument.getDocumentId());
                        if (deleted > 0) {
                            success += deleted;
                            LOG.info("Deleted messageDocument did={}", messageDocument.getDocumentId());
                        } else {
                            failure++;
                            LOG.error("Failed to deleted did={}", messageDocument.getDocumentId());
                        }
                    } else {
                        skipped++;
                    }
                }
            } catch(Exception e) {
                LOG.error("Error during deleting orphan messageDocument, reason={}", e.getLocalizedMessage(), e);
            } finally {
                cronStats.addStats("pendingDocuments", count);
                cronStats.addStats("success", success);
                cronStats.addStats("skipped", skipped);
                cronStats.addStats("failure", failure);
                cronStatsService.save(cronStats);

                LOG.info("Orphan messageDocument count={} success={} skipped={} failure={}",
                        count, success, skipped, failure);
            }
        } else {
            LOG.info("feature is {}", messageDocumentOrphanReportSwitch);
        }
    }
}
