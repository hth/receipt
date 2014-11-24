package com.receiptofi.service;

import com.receiptofi.repository.MessageDocumentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 11/24/14 3:04 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Service
public class MessageDocumentService {

    @Autowired private MessageDocumentManager messageDocumentManager;

    public void resetDocumentsToInitialState(String receiptUserId) {
        messageDocumentManager.resetDocumentsToInitialState(receiptUserId);
    }
}
