package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * User: hitender
 * Date: 8/25/14 12:17 AM
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
//@JsonInclude (JsonInclude.Include.NON_NULL)
@Mobile
public class JsonBizStore {

    @JsonProperty ("address")
    private String address = "";

    @JsonProperty ("phone")
    private String phone = "";

    @JsonProperty ("lat")
    private String lat = "";

    @JsonProperty ("lng")
    private String lng = "";

    @JsonProperty ("type")
    private String type = "";

    @JsonProperty ("rating")
    private float rating;

    @JsonProperty ("country")
    private String country = "";

    private JsonBizStore(BizStoreEntity bizStore) {
        this.address = bizStore.getAddress();
        this.phone = bizStore.getPhoneFormatted();
        if (null != bizStore.getCoordinate()) {
            this.lat = Double.toString(bizStore.getLat());
            this.lng = Double.toString(bizStore.getLng());
        }
        this.type = Arrays.toString(bizStore.getPlaceType());
        this.rating = bizStore.getPlaceRating();

        if (StringUtils.isNotBlank(bizStore.getCountryShortName())) {
            this.country = bizStore.getCountryShortName();
        }
    }

    public static JsonBizStore newInstance(BizStoreEntity bizStore) {
        return new JsonBizStore(bizStore);
    }
}
