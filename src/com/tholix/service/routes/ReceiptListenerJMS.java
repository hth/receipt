/**
 *
 */
package com.tholix.service.routes;

import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.repository.MessageManager;

/**
 * @author hitender
 * @when Mar 30, 2013 11:46:45 AM
 *
 */
@Component
public class ReceiptListenerJMS {
	private static final Logger log = Logger.getLogger(ReceiptListenerJMS.class);

    @Autowired
    MessageManager messageManager;

	public void receive(Map<String, Object> message) throws Exception {
		String id = (String) message.get("id");
		String description = (String) message.get("description");
		String level = (String) message.get("level");

        UserLevelEnum levelEnum = UserLevelEnum.valueOf(level.toUpperCase());
        MessageReceiptEntityOCR object = MessageReceiptEntityOCR.newInstance(id, description, levelEnum);
        messageManager.save(object);

		log.info("Message received: " + id + ", description: " + description + ", user level: " + level + ", and persisted with id: " + object.getId());
	}
}
