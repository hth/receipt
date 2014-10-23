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
@Component
public class PurgeDocumentsProcess {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeDocumentsProcess.class);

    @Value ("${purgeRejectedDocumentAfterDay:15}")
    private int purgeRejectedDocumentAfterDay;

    @Value ("${purgeMaxDocumentsADay:1}")
    private int purgeMaxDocumentsADay;

    //TODO(hth) add to AOP to turn on and off instead
    @Value ("${purgeRejectedDocument:ON}")
    private String purgeRejectedDocument;

    @Autowired private DocumentManager documentManager;

    @Scheduled (cron = "0 0 0 * * ?")
    public void purgeRejectedDocument() {
        LOG.info("begins");
        if (purgeRejectedDocument.equalsIgnoreCase("ON")) {
            int count = 0, found = 0;
            try {
                List<DocumentEntity> documents = documentManager.getAllRejected(purgeRejectedDocumentAfterDay);
                found = documents.size();
                for (DocumentEntity documentEntity : documents) {
                    documentManager.deleteHard(documentEntity);
                    count++;

                    if(count == purgeMaxDocumentsADay && purgeMaxDocumentsADay > 0) {
                        LOG.info("reached purge documents per day max={}", purgeMaxDocumentsADay);
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
}
