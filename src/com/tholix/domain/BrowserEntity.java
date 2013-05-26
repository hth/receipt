package com.tholix.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 4:01 PM
 */
@Document(collection = "BROWSER")
@CompoundIndexes(value = {
        @CompoundIndex(name = "browser_idx", def = "{'cookieId': 1}", unique=true),
} )
public class BrowserEntity extends BaseEntity {

    @NotNull
    private String cookieId;

    @NotNull
    private String ip;

    @NotNull
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
