package com.tholix.web.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.tholix.domain.ReceiptEntity;

/**
 * User: hitender
 * Date: 4/13/13
 * Time: 10:35 PM
 * http://glassfish.java.net/nonav/javaee5/api/javax/xml/bind/annotation/XmlElements.html
 */
@XmlRootElement(namespace="http://tholix.com/schema/receipt/v1", name="landingView")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlType(propOrder = { "userId", "emailId", "pendingCount", "receipts" })
public final class LandingView extends Base {

    @XmlElement(name = "userId", type = String.class, required = true)
    private String userId;

    @XmlElement(name = "emailId", type = String.class, required = true)
    private String emailId;

    @XmlElement(name = "pendingCount", type = Long.class, required = false)
    private long pendingCount;

    @XmlElementWrapper(name = "receipts")
    @XmlElement(name = "receipt")
    protected List<ReceiptEntity> receipts;

    public LandingView() { }

    private LandingView(String userId, String emailId, Header header) {
        super.setHeader(header);
        this.userId = userId;
        this.emailId = emailId;
    }

    public static LandingView newInstance(String userId, String emailId, Header header) {
        return new LandingView(userId, emailId, header);
    }

    public String getEmailId() {
        return emailId;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public void setReceipts(List<ReceiptEntity> receipts) {
        this.receipts = receipts;
    }
}
