package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.annotation.Mobile;

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
    private String address;

    @JsonProperty ("phone")
    private String phone;

    private JsonBizStore(BizStoreEntity bizStoreEntity) {
        this.address = bizStoreEntity.getAddress();
        this.phone = bizStoreEntity.getPhoneFormatted();
    }

    public static JsonBizStore newInstance(BizStoreEntity bizStoreEntity) {
        return new JsonBizStore(bizStoreEntity);
    }
}
