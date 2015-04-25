package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 4:01 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BROWSER")
@CompoundIndexes (value = {
        @CompoundIndex (name = "browser_idx", def = "{'U': -1}", unique = true),
})
public class BrowserEntity extends BaseEntity {

    @Field ("CK")
    private String cookieId;

    @Field ("IP")
    private String ipAddress;

    @Field ("UA")
    private String userAgent;

    @Field ("CA")
    String category;

    @Field ("FA")
    String family;

    @Field ("OS")
    String osFamilyName;

    @Field ("VN")
    String versionNumber;

    private BrowserEntity(
            String cookieId,
            String ipAddress,
            String userAgent,
            String category,
            String family,
            String osFamilyName,
            String versionNumber
    ) {
        super();
        this.cookieId = cookieId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.category = category;
        this.family = family;
        this.osFamilyName = osFamilyName;
        this.versionNumber = versionNumber;
    }

    public static BrowserEntity newInstance(
            String cookieId,
            String ip,
            String userAgent,
            String category,
            String family,
            String osFamilyName,
            String versionNumber
    ) {
        return new BrowserEntity(cookieId, ip, userAgent, category, family, osFamilyName, versionNumber);
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
