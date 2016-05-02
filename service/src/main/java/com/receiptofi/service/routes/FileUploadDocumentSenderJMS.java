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
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;

/**
 * @author hitender
 * @since Mar 30, 2013 2:42:21 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public final class FileUploadDocumentSenderJMS {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadDocumentSenderJMS.class);

    private String queueName;
    private JmsTemplate jmsSenderTemplate;

    private FileUploadDocumentSenderJMS() {}

    @Autowired
    public FileUploadDocumentSenderJMS(
            @Value ("${queue-name}")
            String queueName,

            JmsTemplate jmsSenderTemplate) {
        this.queueName = queueName;
        this.jmsSenderTemplate = jmsSenderTemplate;
    }

    public void send(final DocumentEntity documentEntity, final UserProfileEntity userProfile) {
        jmsSenderTemplate.send(queueName,
                session -> {
                    MapMessage mapMessage = session.createMapMessage();
                    mapMessage.setString("id", documentEntity.getId());
                    mapMessage.setString("level", userProfile.getLevel().name());
                    mapMessage.setString("status", documentEntity.getDocumentStatus().name());
                    mapMessage.setJMSTimestamp(documentEntity.getUpdated().getTime());
                    return mapMessage;
                }
        );
        LOG.info("Message sent ReceiptOCR={}, level={}", documentEntity.getId(), userProfile.getLevel().getDescription());
    }
}
