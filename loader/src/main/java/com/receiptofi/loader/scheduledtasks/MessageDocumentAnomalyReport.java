package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.MessageDocumentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * User: hitender
 * Date: 4/14/15 2:15 AM
 */
public class MessageDocumentAnomalyReport {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemProcess.class);

    private MessageDocumentService messageDocumentService;
    private DocumentUpdateService documentUpdateService;

    @Autowired
    public MessageDocumentAnomalyReport(
            MessageDocumentService messageDocumentService,
            DocumentUpdateService documentUpdateService
    ) {
        this.messageDocumentService = messageDocumentService;
        this.documentUpdateService = documentUpdateService;
    }

    @Scheduled (cron = "${loader.MessageDocumentAnomalyReport.orphanMessageDocument}")
    public void orphanMessageDocument() {
        List<MessageDocumentEntity> pendingDocuments = messageDocumentService.findAllPending();
        for (MessageDocumentEntity messageDocument : pendingDocuments) {
            DocumentEntity document = documentUpdateService.loadActiveDocumentById(messageDocument.getDocumentId());
            if (null == document) {
                LOG.error("Orphan Message DocumentId={} messageDocumentId={}",
                        messageDocument.getDocumentId(), messageDocument.getId());
            }
        }
    }
}
