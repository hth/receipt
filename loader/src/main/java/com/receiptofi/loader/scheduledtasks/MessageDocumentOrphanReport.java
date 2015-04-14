package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.MessageDocumentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemProcess.class);

    private String messageDocumentOrphanReport;
    private MessageDocumentService messageDocumentService;
    private DocumentUpdateService documentUpdateService;

    @Autowired
    public MessageDocumentOrphanReport(
            @Value ("${messageDocumentOrphanReport:ON}")
            String messageDocumentOrphanReport,

            MessageDocumentService messageDocumentService,
            DocumentUpdateService documentUpdateService
    ) {
        this.messageDocumentOrphanReport = messageDocumentOrphanReport;
        this.messageDocumentService = messageDocumentService;
        this.documentUpdateService = documentUpdateService;
    }

    @Scheduled (cron = "${loader.MessageDocumentOrphanReport.orphanMessageDocument}")
    public void orphanMessageDocument() {
        if (messageDocumentOrphanReport.equals("ON")) {
            List<MessageDocumentEntity> pendingDocuments = messageDocumentService.findAllPending();

            int count = pendingDocuments.size(), success = 0, failure = 0;
            try {
                for (MessageDocumentEntity messageDocument : pendingDocuments) {
                    DocumentEntity document = documentUpdateService.loadActiveDocumentById(messageDocument.getDocumentId());
                    if (null == document) {
                        LOG.error("Orphan Message DocumentId={} messageDocumentId={}",
                                messageDocument.getDocumentId(), messageDocument.getId());
                        int deleted = messageDocumentService.deleteAllForReceiptOCR(messageDocument.getDocumentId());
                        if (deleted > 0) {
                            success += deleted;
                            LOG.info("Deleted messageDocument did={}", messageDocument.getDocumentId());
                        } else {
                            failure++;
                            LOG.error("Failed to deleted did={}", messageDocument.getDocumentId());
                        }
                    }
                }
            } catch(Exception e) {
                LOG.error("Error during deleting orphan messageDocument, reason={}", e.getLocalizedMessage(), e);
            } finally {
                LOG.info("Orphan messageDocument count={} success={} failure={}", count, success, failure);
            }
        } else {
            LOG.info("feature is {}", messageDocumentOrphanReport);
        }
    }
}
