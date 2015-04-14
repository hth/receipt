package com.receiptofi.service.wrapper;

/**
 * User: hitender
 * Date: 4/13/15 11:57 PM
 */
public class DocumentProcessBy {
    private String name;
    private String mail;
    private String rid;

    public DocumentProcessBy(String name, String mail, String rid) {
        this.name = name;
        this.mail = mail;
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getRid() {
        return rid;
    }
}
