package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.common.base.Objects;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 9/25/15 2:39 AM
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
@Mobile
public class JsonFriend {

    @JsonProperty ("rid")
    private String rid;

    @JsonProperty ("initials")
    private String initials;

    @JsonProperty ("name")
    private String name;

    public JsonFriend(UserProfileEntity userProfile) {
        this.rid = userProfile.getReceiptUserId();
        this.initials = userProfile.getInitials();
        this.name = userProfile.getName();
    }

    public JsonFriend(String rid, String initials, String name) {
        this.rid = rid;
        this.initials = initials;
        this.name = name;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonFriend that = (JsonFriend) o;
        return Objects.equal(rid, that.rid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rid);
    }
}
