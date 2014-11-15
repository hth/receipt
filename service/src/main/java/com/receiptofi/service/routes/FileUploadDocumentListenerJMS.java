/**
 *
 */
package com.receiptofi.service.routes;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.MessageManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hitender
 * @since Mar 30, 2013 11:46:45 AM
 */
@Component
public final class FileUploadDocumentListenerJMS {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadDocumentListenerJMS.class);

    @Autowired private MessageManager messageManager;

    public void receive(Map<String, Object> message) throws Exception {
        String id = (String) message.get("id");
        String level = (String) message.get("level");
        int status = (Integer) message.get("status");
        DocumentStatusEnum documentStatusEnum = DocumentStatusEnum.OCR_PROCESSED;

        switch (status) {
            case 0:
                documentStatusEnum = DocumentStatusEnum.OCR_PROCESSED;
                break;
            case 1:
                documentStatusEnum = DocumentStatusEnum.TURK_PROCESSED;
                break;
            case 2:
                documentStatusEnum = DocumentStatusEnum.TURK_REQUEST;
                break;
            default:
                LOG.error("Reached unreachable condition, status={}", status);
                throw new RuntimeException("Reached unreachable condition " + status);
        }

        UserLevelEnum levelEnum = UserLevelEnum.valueOf(level);
        MessageDocumentEntity object = MessageDocumentEntity.newInstance(id, levelEnum, documentStatusEnum);
        messageManager.save(object);

        LOG.info("Message received id={}, user level={}, and persisted with id={}", id, level, object.getId());
    }
}
