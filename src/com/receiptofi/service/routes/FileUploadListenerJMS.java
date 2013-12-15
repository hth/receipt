/**
 *
 */
package com.receiptofi.service.routes;

import com.receiptofi.domain.MessageReceiptEntityOCR;
import com.receiptofi.domain.types.ReceiptStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.MessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hitender
 * @since Mar 30, 2013 11:46:45 AM
 *
 */
@Component
public final class FileUploadListenerJMS {
	private static final Logger log = LoggerFactory.getLogger(FileUploadListenerJMS.class);

    @Autowired private MessageManager messageManager;

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

        //TODO write unit test to make sure the new levels are added as per the format. Use Camel Format
        /** Required to match the name for User Level */
        String matchingName = StringUtils.replace(level, " ", "_");
        UserLevelEnum levelEnum = UserLevelEnum.valueOf(matchingName.toUpperCase());
        MessageReceiptEntityOCR object = MessageReceiptEntityOCR.newInstance(id, levelEnum, receiptStatusEnum);
        messageManager.save(object);

		log.info("Message received: " + id + ", user level: " + level + ", and persisted with id: " + object.getId());
	}
}
