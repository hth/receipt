package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field ("BR")
    private String browser;

    @Field ("BRV")
    private String browserVersion;

    @Field ("DV")
    private String device;

    @Field ("DVB")
    private String deviceBrand;

    @Field ("OS")
    private String operatingSystem;

    @Field ("OSV")
    private String operatingSystemVersion;

    @SuppressWarnings("unused")
    private BrowserEntity() {}

    private BrowserEntity(
            String cookieId,
            String ipAddress,
            String userAgent,
            String browser,
            String browserVersion,
            String device,
            String deviceBrand,
            String operatingSystem,
            String operatingSystemVersion
    ) {
        super();
        this.cookieId = cookieId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.device = device;
        this.deviceBrand = deviceBrand;
        this.operatingSystem = operatingSystem;
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public static BrowserEntity newInstance(
            String cookieId,
            String ip,
            String userAgent,
            String browser,
            String browserVersion,
            String device,
            String deviceBrand,
            String operatingSystem,
            String operatingSystemVersion
    ) {
        return new BrowserEntity(cookieId, ip, userAgent, browser, browserVersion, device, deviceBrand, operatingSystem, operatingSystemVersion);
    }
}
