package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.BizNameEntity;

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
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BizName {

    @JsonProperty ("name")
    private String businessName;

    private BizName(BizNameEntity bizNameEntity) {
        this.businessName = bizNameEntity.getBusinessName();
    }

    public static BizName newInstance(BizNameEntity bizNameEntity) {
        return new BizName(bizNameEntity);
    }
}
