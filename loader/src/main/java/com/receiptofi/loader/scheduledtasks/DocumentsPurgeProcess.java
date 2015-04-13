package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.repository.DocumentManager;

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

    private int purgeRejectedDocumentAfterDay;
    private int purgeMaxDocumentsADay;

    //TODO(hth) add to AOP to turn on and off instead
    private String purgeRejectedDocument;

    private int count;

    @Autowired
    public DocumentsPurgeProcess(
            @Value ("${purgeRejectedDocumentAfterDay:15}")
            int purgeRejectedDocumentAfterDay,

            @Value ("${purgeMaxDocumentsADay:10000}")
            int purgeMaxDocumentsADay,

            @Value ("${purgeRejectedDocument:ON}")
            String purgeRejectedDocument,

            DocumentManager documentManager
    ) {
        this.purgeRejectedDocumentAfterDay = purgeRejectedDocumentAfterDay;
        this.purgeMaxDocumentsADay = purgeMaxDocumentsADay;
        this.purgeRejectedDocument = purgeRejectedDocument;
        this.documentManager = documentManager;
    }

    @Scheduled (cron = "${loader.DocumentsPurgeProcess.purgeRejectedDocument}")
    public void purgeRejectedDocument() {
        LOG.info("begins");
        if ("ON".equalsIgnoreCase(purgeRejectedDocument)) {
            int found = 0;
            try {
                List<DocumentEntity> documents = documentManager.getAllRejected(purgeRejectedDocumentAfterDay);
                found = documents.size();
                for (DocumentEntity documentEntity : documents) {
                    documentManager.deleteHard(documentEntity);
                    count++;

                    if (purgeMaxDocumentsADay > 0 && count == purgeMaxDocumentsADay) {
                        LOG.info("Reached purge documents per day max={}", purgeMaxDocumentsADay);
                        break;
                    }
                }
            } catch (Exception e) {
                LOG.error("error purge document, reason={}", e.getLocalizedMessage(), e);
            } finally {
                LOG.info("complete deleted={}, found={}", count, found);
            }
        } else {
            LOG.info("feature is {}", purgeRejectedDocument);
        }
    }

    /**
     * Counts number of rejected documents deleted
     */
    protected int getCount() {
        return count;
    }
}