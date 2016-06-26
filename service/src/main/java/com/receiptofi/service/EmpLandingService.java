package com.receiptofi.service;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.repository.MessageDocumentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 8:12 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class EmpLandingService {

    private MessageDocumentManager messageDocumentManager;

    @Autowired
    public EmpLandingService(MessageDocumentManager messageDocumentManager) {
        this.messageDocumentManager = messageDocumentManager;
    }

    public List<MessageDocumentEntity> pendingReceipts(String emailId, String profileId, DocumentStatusEnum status) {
        return messageDocumentManager.findPending(emailId, profileId, status);
    }

    public List<MessageDocumentEntity> queuedReceipts(String emailId, String profileId) {
        return messageDocumentManager.findUpdateWithLimit(emailId, profileId, DocumentStatusEnum.PENDING);
    }

    public List<MessageDocumentEntity> recheck(String emailId, String profileId) {
        return messageDocumentManager.findUpdateWithLimit(emailId, profileId, DocumentStatusEnum.REPROCESS);
    }

    public void delete(MessageDocumentEntity messageDocumentEntity) {
        messageDocumentManager.deleteHard(messageDocumentEntity);
    }
}
