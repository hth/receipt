package com.tholix.web.form;

import java.util.Date;

import com.tholix.domain.types.UserLevelEnum;

/**
 * User: hitender
 * Date: 4/7/13
 * Time: 1:25 PM
 * @deprecated Not being used and was never used. Was suppose to be used for employee Receipt Message
 */
public class MessageReceiptForm {

    private String id;
    private String idReceiptOCR;
    private String description;
    private UserLevelEnum level;
    private Date created;

    private MessageReceiptForm() { }

    private MessageReceiptForm(String id, String idReceiptOCR, String description, UserLevelEnum level, Date created) {
        this.id = id;
        this.idReceiptOCR = idReceiptOCR;
        this.description = description;
        this.level = level;
        this.created = created;
    }

    public static MessageReceiptForm newInstance() {
        return new MessageReceiptForm();
    }

    public static MessageReceiptForm newInstance(String id, String idReceiptOCR, String description, UserLevelEnum level, Date created) {
        return new MessageReceiptForm(id, idReceiptOCR, description, level, created);
    }

    public String getId() {
        return id;
    }

    public String getIdReceiptOCR() {
        return idReceiptOCR;
    }

    public String getDescription() {
        return description;
    }

    public UserLevelEnum getLevel() {
        return level;
    }

    public Date getCreated() {
        return created;
    }
}
