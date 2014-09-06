package com.receiptofi.web.listener;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 9/6/14 1:52 PM
 */
@Component
public class ReceiptofiInitializationCheckBean {
    private static final Logger log = LoggerFactory.getLogger(ReceiptofiInitializationCheckBean.class);

    @Autowired private JmsTemplate jmsSenderTemplate;

    @Value ("${queue-name}")
    private String queueName;

    @PostConstruct
    public void checkActiveMQ() {
        try {
            jmsSenderTemplate.getConnectionFactory().createConnection();
            log.info("ActiveMQ messaging is available");
        } catch(Exception e) {
            log.error("ActiveMQ messaging is unavailable reason={}", e.getLocalizedMessage(), e);
            stopServer();
        }
    }

    private void stopServer() {
        log.error("Stopping server now. Fix above failures.");
        System.exit(0);
    }
}
