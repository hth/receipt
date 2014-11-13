/**
 *
 */
package com.receiptofi.service.routes;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.UserProfileEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author hitender
 * @since Mar 30, 2013 2:42:21 AM
 */
@Component
public final class FileUploadDocumentSenderJMS {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadDocumentSenderJMS.class);

    @Autowired private JmsTemplate jmsSenderTemplate;

    @Value ("${queue-name}")
    private String queueName;

    public void send(final DocumentEntity documentEntity, final UserProfileEntity userProfile) {
        jmsSenderTemplate.send(queueName,
                new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        MapMessage mapMessage = session.createMapMessage();
                        mapMessage.setString("id", documentEntity.getId());
                        mapMessage.setString("level", userProfile.getLevel().name());
                        mapMessage.setInt("status", documentEntity.getDocumentStatus().ordinal());
                        mapMessage.setJMSTimestamp(documentEntity.getUpdated().getTime());
                        return mapMessage;
                    }
                }
        );
        LOG.info("Message sent ReceiptOCR={}, level={}", documentEntity.getId(), userProfile.getLevel().getDescription());
    }
}
