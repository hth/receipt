package com.receiptofi.domain.json;

import static com.receiptofi.domain.json.JsonReceipt.ISO8601_FMT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.CouponEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 4/26/16 7:56 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonInclude (JsonInclude.Include.NON_NULL)
@Mobile
public class JsonCoupon {
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("rid")
    private String rid;

    @JsonProperty ("lid")
    private String lid;

    @JsonProperty ("bn")
    private String businessName;

    @JsonProperty ("ft")
    private String freeText;

    @JsonProperty ("av")
    private String available;

    @JsonProperty ("ex")
    private String expire;

    @JsonProperty ("ct")
    private String couponType;

    @JsonProperty ("c")
    private String created;

    @JsonProperty ("u")
    private String updated;

    @JsonProperty ("rm")
    private boolean reminder;

    @JsonProperty ("ip")
    private String imagePath;

    @JsonProperty ("sh")
    private List<String> sharedWithRids = new ArrayList<>();

    @JsonProperty ("oi")
    private String originId;

    @JsonProperty ("uc")
    private boolean usedCoupon;

    @JsonProperty ("a")
    private boolean active;

    private JsonCoupon(CouponEntity coupon) {
        this.id = coupon.getId();
        this.rid = coupon.getRid();
        this.lid = coupon.getLocalId();
        this.businessName = coupon.getBusinessName();
        this.freeText = coupon.getFreeText();
        this.available = DateFormatUtils.format(coupon.getAvailable(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.expire = DateFormatUtils.format(coupon.getExpire(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.created = DateFormatUtils.format(coupon.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.updated = DateFormatUtils.format(coupon.getUpdated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.couponType = coupon.getCouponType().getName();
        this.reminder = coupon.isReminder();
        this.imagePath = coupon.getImagePath();
        this.sharedWithRids = coupon.getSharedWithRids();
        this.originId = coupon.getOriginId();
        this.usedCoupon = coupon.isUsedCoupon();
        this.active = coupon.isActive();
    }

    public static JsonCoupon newInstance(CouponEntity coupon) {
        return new JsonCoupon(coupon);
    }

}
