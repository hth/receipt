package com.receiptofi.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 9/10/14 1:06 PM
 */
@Component
public class PurgeDocumentsProcess {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeDocumentsProcess.class);

    @Value ("${purgeRejectedDocumentAfterDay:7}")
    private int purgeRejectedDocumentAfterDay;

    @Value ("${purgeMaxDocumentsADay:1}")
    private int purgeMaxDocumentsADay;

    //TODO(hth) add to AOP to turn on and off instead
    @Value("${purgeRejectedDocument:ON}")
    private String purgeRejectedDocument;

    @Scheduled (cron="0 0 0 * * ?")
    public void purgeRejectedDocument() {
        LOG.info("begins");

    }

}
