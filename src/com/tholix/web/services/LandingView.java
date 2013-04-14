package com.tholix.web.services;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.tholix.domain.ReceiptEntity;

/**
 * User: hitender
 * Date: 4/13/13
 * Time: 10:35 PM
 */
@XmlRootElement
public class LandingView implements Serializable {

    private String userId;
    private String emailId;
    private long pendingCount;
    private List<ReceiptEntity> receipts;

    private LandingView() { }

    private LandingView(String userId, String emailId) {
        this.userId = userId;
        this.emailId = emailId;
    }

    public static LandingView newInstance(String userId, String emailId) {
        return new LandingView(userId, emailId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public List<ReceiptEntity> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<ReceiptEntity> receipts) {
        this.receipts = receipts;
    }
}
