package com.receiptofi.service;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
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

    @Autowired private MessageDocumentManager messageDocumentManager;

    public void resetDocumentsToInitialState(String receiptUserId) {
        messageDocumentManager.resetDocumentsToInitialState(receiptUserId);
    }

    public List<MessageDocumentEntity> findAllPending(Date since) {
        return messageDocumentManager.findAllPending(since);
    }

    public int deleteAllForReceiptOCR(String did) {
        return messageDocumentManager.deleteAllForReceiptOCR(did);
    }

    public void markMessageForReceiptAsDuplicate(String did, String emailId, String rid) {
        messageDocumentManager.markMessageForReceiptAsDuplicate(did, emailId, rid, DocumentStatusEnum.REJECT);
    }
}
