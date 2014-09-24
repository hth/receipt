package com.receiptofi.web.scheduledtasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 9/10/14 1:06 PM
 */
@Component
public class PurgeDocumentsProcess {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeDocumentsProcess.class);

    @Value ("${purgeRejectedDocumentAfterDay:7}")
    private int purgeRejectedDocumentAfterDay;

    @Scheduled (cron="0 0 0 * * ?")
    public void purgeRejectedDocument() {

    }

}
