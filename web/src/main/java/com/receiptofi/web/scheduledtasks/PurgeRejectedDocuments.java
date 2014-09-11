package com.receiptofi.web.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 9/10/14 1:06 PM
 */
@Component
public class PurgeRejectedDocuments {
    private static final Logger log = LoggerFactory.getLogger(PurgeRejectedDocuments.class);

    @Value ("${purgeRejectedDocumentAfterDay:7}")
    private int purgeRejectedDocumentAfterDay;

}
