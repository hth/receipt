package com.receiptofi.service;

import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.repository.MessageDocumentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<MessageDocumentEntity> findAllPending() {
        return messageDocumentManager.findAllPending();
    }
}
