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

    public List<MessageDocumentEntity> pendingReceipts(String email, String rid, DocumentStatusEnum status) {
        return messageDocumentManager.findPending(email, rid, status);
    }

    public List<MessageDocumentEntity> queuedReceipts(String email, String rid) {
        return messageDocumentManager.findUpdateWithLimit(email, rid, DocumentStatusEnum.PENDING);
    }

    public List<MessageDocumentEntity> recheck(String email, String rid) {
        return messageDocumentManager.findUpdateWithLimit(email, rid, DocumentStatusEnum.REPROCESS);
    }

    public void delete(MessageDocumentEntity messageDocument) {
        messageDocumentManager.deleteHard(messageDocument);
    }
}
