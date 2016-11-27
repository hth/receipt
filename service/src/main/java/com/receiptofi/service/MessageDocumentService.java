package com.receiptofi.service;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.repository.MessageDocumentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/24/14 3:04 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class MessageDocumentService {

    private MessageDocumentManager messageDocumentManager;

    @Autowired
    public MessageDocumentService(MessageDocumentManager messageDocumentManager) {
        this.messageDocumentManager = messageDocumentManager;
    }

    public void resetDocumentsToInitialState(String receiptUserId) {
        messageDocumentManager.resetDocumentsToInitialState(receiptUserId);
    }

    public List<MessageDocumentEntity> findAllPending(Date since) {
        return messageDocumentManager.findAllPending(since);
    }

    public int deleteAllForReceiptOCR(String did) {
        return messageDocumentManager.deleteAllForReceiptOCR(did);
    }

    public void lockMessageWhenDuplicate(String did, String emailId, String rid) {
        messageDocumentManager.lockMessageWhenDuplicate(did, emailId, rid);
    }
}
