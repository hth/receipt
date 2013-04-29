package com.tholix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.MessageReceiptEntityOCR;
import com.tholix.repository.MessageManager;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 8:12 PM
 */
@Service
public class EmpLandingService {

    @Autowired private MessageManager messageManager;

    public List<MessageReceiptEntityOCR> pendingReceipts(String emailId, String profileId) {
        return messageManager.findPending(emailId, profileId);
    }

    public List<MessageReceiptEntityOCR> queuedReceipts(String emailId, String profileId) {
        return messageManager.findUpdateWithLimit(emailId, profileId);
    }
}
