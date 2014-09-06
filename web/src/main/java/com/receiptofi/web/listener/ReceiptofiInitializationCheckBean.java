package com.receiptofi.web.listener;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Checks if all vital setup are running before starting server
 *
 * User: hitender
 * Date: 9/6/14 1:52 PM
 */
@Component
public class ReceiptofiInitializationCheckBean {
    private static final Logger log = LoggerFactory.getLogger(ReceiptofiInitializationCheckBean.class);

    @Autowired private JmsTemplate jmsSenderTemplate;

    @PostConstruct
    public void checkActiveMQ() {
        try {
            jmsSenderTemplate.getConnectionFactory().createConnection();
            log.info("ActiveMQ messaging is available");
        } catch(JMSException e) {
            log.error("ActiveMQ messaging is unavailable reason={}", e.getLocalizedMessage(), e);
            stopServer();
        }
    }

    private void stopServer() {
        log.error("Stopping server now. Fix above failures.");
        System.exit(0);
    }
}
