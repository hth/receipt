package com.receiptofi.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 4:01 PM
 */
@Document(collection = "BROWSER")
@CompoundIndexes(value = {
        @CompoundIndex(name = "browser_idx", def = "{'U': 1}", unique=true),
} )
public class BrowserEntity extends BaseEntity {

    @NotNull
    @Field("COOKIE")
    private String cookieId;

    @NotNull
    @Field("IP")
    private String ip;

    @NotNull
    @Field("USER_AGENT")
    private String userAgent;

    private BrowserEntity(String cookieId, String ip, String userAgent) {
        this.cookieId = cookieId;
        this.ip = ip;
        this.userAgent = userAgent;
    }

    public static BrowserEntity newInstance(String cookieId, String ip, String userAgent) {
        return new BrowserEntity(cookieId, ip, userAgent);
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
