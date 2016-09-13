package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.annotation.Mobile;

import javax.validation.constraints.Null;

/**
 * User: hitender
 * Date: 10/3/15 3:23 AM
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
public class JsonOweExpenses {

    /** When object used for OwesOther then RID is not null and FID is null. */
    @Null
    @JsonProperty ("rid")
    private String receiptUserId;

    /** When object used for OweMe then RID is not and FID is not null. */
    @Null
    @JsonProperty ("fid")
    private String friendUserId;

    @JsonProperty ("splitTotal")
    private Double splitTotal;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("cs")
    private String countryShortName;

    public JsonOweExpenses(String receiptUserId, String friendUserId, Double splitTotal, String name, String countryShortName) {
        this.receiptUserId = receiptUserId;
        this.friendUserId = friendUserId;
        this.splitTotal = splitTotal;
        this.name = name;
        this.countryShortName = countryShortName;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public Double getSplitTotal() {
        return splitTotal;
    }

    public String getName() {
        return name;
    }

    public String getCountryShortName() {
        return countryShortName;
    }
}
