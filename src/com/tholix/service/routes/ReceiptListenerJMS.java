/**
 *
 */
package com.tholix.service.routes;

import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.repository.MessageManager;

/**
 * @author hitender
 * @since Mar 30, 2013 11:46:45 AM
 *
 */
@Component
public class ReceiptListenerJMS {
	private static final Logger log = Logger.getLogger(ReceiptListenerJMS.class);

    @Autowired
    MessageManager messageManager;

	public void receive(Map<String, Object> message) throws Exception {
		String id = (String) message.get("id");
		String level = (String) message.get("level");
        int status = (Integer) message.get("status");
        ReceiptStatusEnum receiptStatusEnum = ReceiptStatusEnum.OCR_PROCESSED;

        switch(status) {
            case 0:
                receiptStatusEnum = ReceiptStatusEnum.OCR_PROCESSED;
                break;
            case 1:
                receiptStatusEnum = ReceiptStatusEnum.TURK_PROCESSED;
                break;
            case 2:
                receiptStatusEnum = ReceiptStatusEnum.TURK_REQUEST;
                break;
        }

        UserLevelEnum levelEnum = UserLevelEnum.valueOf(level.toUpperCase());
        MessageReceiptEntityOCR object = MessageReceiptEntityOCR.newInstance(id, levelEnum, receiptStatusEnum);
        messageManager.save(object);

		log.info("Message received: " + id + ", user level: " + level + ", and persisted with id: " + object.getId());
	}
}
