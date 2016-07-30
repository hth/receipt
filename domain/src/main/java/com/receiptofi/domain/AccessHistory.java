package com.receiptofi.domain;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * User: hitender
 * Date: 7/24/16 10:15 AM
 */
public class AccessHistory implements Serializable {

    @Field ("IP")
    private String ip;

    @Field ("DT")
    private Date date;

    public AccessHistory(String ip, Date date) {
        this.ip = ip;
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public Date getDate() {
        return date;
    }
}
