package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.service.CronStatsService;
import com.receiptofi.service.DocumentUpdateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: hitender
 * Date: 9/10/14 1:06 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class DocumentsPurgeProcess {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsPurgeProcess.class);

    private DocumentManager documentManager;
    private DocumentUpdateService documentUpdateService;
    private CronStatsService cronStatsService;

    private CronStatsEntity cronStats;

    private int purgeRejectedDocumentAfterDay;
    private int purgeMaxDocumentsADay;

    //TODO(hth) add to AOP to turn on and off instead
    private String purgeRejectedDocument;

    @Autowired
    public DocumentsPurgeProcess(
            @Value ("${purgeRejectedDocumentAfterDay:1}")
            int purgeRejectedDocumentAfterDay,

            @Value ("${purgeMaxDocumentsADay:10000}")
            int purgeMaxDocumentsADay,

            @Value ("${purgeRejectedDocument:ON}")
            String purgeRejectedDocument,

            DocumentManager documentManager,
            DocumentUpdateService documentUpdateService,
            CronStatsService cronStatsService
    ) {
        this.purgeRejectedDocumentAfterDay = purgeRejectedDocumentAfterDay;
        this.purgeMaxDocumentsADay = purgeMaxDocumentsADay;
        this.purgeRejectedDocument = purgeRejectedDocument;
        this.documentManager = documentManager;
        this.documentUpdateService = documentUpdateService;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (cron = "${loader.DocumentsPurgeProcess.purgeRejectedDocument}")
    public void purgeRejectedDocument() {
        LOG.info("Purging Rejected document begins");

        cronStats = new CronStatsEntity(
                DocumentsPurgeProcess.class.getName(),
                "Purge_Rejected_Document",
                purgeRejectedDocument);

        if ("ON".equalsIgnoreCase(purgeRejectedDocument)) {
            int found = 0, failure = 0, deleted = 0;
            try {
                List<DocumentEntity> documents = documentManager.getAllRejected(purgeRejectedDocumentAfterDay);
                found = documents.size();
                for (DocumentEntity documentEntity : documents) {
                    documentUpdateService.deleteRejectedDocument(documentEntity);
                    deleted++;

                    if (purgeMaxDocumentsADay > 0 && deleted == purgeMaxDocumentsADay) {
                        LOG.info("Reached purge documents per day max={}", purgeMaxDocumentsADay);
                        break;
                    }
                }
            } catch (Exception e) {
                LOG.error("error purge document, reason={}", e.getLocalizedMessage(), e);
                failure++;
            } finally {
                cronStats.addStats("found", found);
                cronStats.addStats("failure", failure);
                cronStats.addStats("deleted", deleted);
                cronStatsService.save(cronStats);

                LOG.info("Purging Rejected Document complete found={} failure={} deleted={}", found, failure, deleted);
            }
        } else {
            LOG.info("feature is {}", purgeRejectedDocument);
        }
    }

    protected CronStatsEntity getCronStats() {
        return cronStats;
    }
}
