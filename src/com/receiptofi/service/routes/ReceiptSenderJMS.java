/**
 *
 */
package com.receiptofi.service.routes;

import com.receiptofi.domain.ReceiptEntityOCR;
import com.receiptofi.domain.UserProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * @author hitender
 * @since Mar 30, 2013 2:42:21 AM
 *
 */
public final class ReceiptSenderJMS {
	private static final Logger log = LoggerFactory.getLogger(ReceiptSenderJMS.class);

	@Autowired private JmsTemplate jmsSenderTemplate;

    @Value("${queue-name}")
    private String queueName;

	public void send(final ReceiptEntityOCR receiptOCR, final UserProfileEntity userProfile) {
        assert(queueName.length() > 0);
        jmsSenderTemplate.send(queueName,
				new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						MapMessage mapMessage = session.createMapMessage();
						mapMessage.setString("id", receiptOCR.getId());
						mapMessage.setString("level", userProfile.getLevel().getDescription());
                        mapMessage.setInt("status", receiptOCR.getReceiptStatus().ordinal());

						//This does not work since this values has to be set after sending the message. It will always default to 4.
						mapMessage.setJMSPriority(userProfile.getLevel().getMessagePriorityJMS());

						mapMessage.setJMSTimestamp(receiptOCR.getUpdated().getTime());
						return mapMessage;
					}
				}
				);
		log.info("Message sent ReceiptOCR - id: "+ receiptOCR.getId()  + ". With level: " + userProfile.getLevel().getDescription());
    }
}
