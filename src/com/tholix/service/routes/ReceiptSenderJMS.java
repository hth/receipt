/**
 * 
 */
package com.tholix.service.routes;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserProfileEntity;

/**
 * @author hitender 
 * @when Mar 30, 2013 2:42:21 AM
 *
 */
public class ReceiptSenderJMS {
	private static final Logger log = Logger.getLogger(ReceiptSenderJMS.class);

	@Autowired
    private JmsTemplate jmsTemplate;
	
	public void send(final ReceiptEntityOCR receiptOCR, final UserProfileEntity userProfile){
		jmsTemplate.send("orderQueue",
				new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						MapMessage mapMessage = session.createMapMessage();
						mapMessage.setString("id", receiptOCR.getId());
						mapMessage.setString("description", receiptOCR.getDescription());
						mapMessage.setString("level", userProfile.getLevel().getDescription());
						
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
