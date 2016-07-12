package com.receiptofi.domain;

import com.receiptofi.domain.types.MailStatusEnum;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 7/10/16 3:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "MAIL")
public class MailEntity extends BaseEntity {

    @Field ("TN")
    private String toName = "";

    @Field ("TM")
    private String toMail;

    @Field ("FN")
    private String fromName;

    @Field ("FM")
    private String fromMail;

    @Field ("ST")
    private String subject;

    @Field ("ME")
    private String message;

    @Field ("MS")
    private MailStatusEnum mailStatus;

    @Field ("AT")
    private int attempts = 0;

    public String getToName() {
        return toName;
    }

    public MailEntity setToName(String toName) {
        this.toName = toName;
        return this;
    }

    public String getToMail() {
        return toMail;
    }

    public MailEntity setToMail(String toMail) {
        this.toMail = toMail;
        return this;
    }

    public String getFromName() {
        return fromName;
    }

    public MailEntity setFromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    public String getFromMail() {
        return fromMail;
    }

    public MailEntity setFromMail(String fromMail) {
        this.fromMail = fromMail;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MailEntity setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MailEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public MailStatusEnum getMailStatus() {
        return mailStatus;
    }

    public MailEntity setMailStatus(MailStatusEnum mailStatus) {
        this.mailStatus = mailStatus;
        return this;
    }

    public int getAttempts() {
        return attempts;
    }

    public void addAttempts() {
        this.attempts++;
    }
}
