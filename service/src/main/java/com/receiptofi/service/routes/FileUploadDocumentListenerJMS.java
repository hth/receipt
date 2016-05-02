/**
 *
 */
package com.receiptofi.service.routes;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.MessageDocumentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hitender
 * @since Mar 30, 2013 11:46:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public final class FileUploadDocumentListenerJMS {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadDocumentListenerJMS.class);

    private MessageDocumentManager messageDocumentManager;

    private FileUploadDocumentListenerJMS() {}

    @Autowired
    public FileUploadDocumentListenerJMS(MessageDocumentManager messageDocumentManager) {
        this.messageDocumentManager = messageDocumentManager;
    }

    public void receive(Map<String, Object> message) throws Exception {
        String documentId = (String) message.get("id");

        String level = (String) message.get("level");
        UserLevelEnum levelEnum = UserLevelEnum.valueOf(level);

        String status = (String) message.get("status");
        DocumentStatusEnum documentStatusEnum = DocumentStatusEnum.valueOf(status);

        MessageDocumentEntity object = MessageDocumentEntity.newInstance(documentId, levelEnum, documentStatusEnum);
        messageDocumentManager.save(object);

        LOG.info("Message received documentId={}, user level={}, and persisted with id={}",
                documentId, level, object.getId());
    }
}
