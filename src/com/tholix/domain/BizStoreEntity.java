package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 10:16 PM
 */
@Document(collection = "BIZ_STORE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "biz_store_idx", def = "{'ADDRESS': 1, 'PHONE': 1}", unique=true),
} )
public class BizStoreEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 128)
    @Field("ADDRESS")
    private String address;

    @NotNull
    @Size(min = 0, max = 20)
    @Field("PHONE")
    private String phone;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Field("LAT")
    private double lat;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Field("LNG")
    private double lng;

    @DBRef
    @Field("BIZ_NAME")
    private BizNameEntity bizName;

    /* To make bean happy */
    public BizStoreEntity() {

    }

    public static BizStoreEntity newInstance() {
        return new BizStoreEntity();
    }

    /**
     * For web display of the address
     *
     * @return
     */
    @Transient
    public String getAddressWrapped() {
        return address.replaceFirst(",", "<br/>");
    }

    public String getAddressWrappedMore() {
        return getAddressWrapped().replaceFirst(",", "<br/>");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = StringUtils.strip(address);
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneFormatted() {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            //Currently defaults to US
            Phonenumber.PhoneNumber numberPrototype = phoneUtil.parse(phone, "US");
            return phoneUtil.format(numberPrototype, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return "";
    }

    /**
     * Remove everything other than numbers. Do the formatting on client side
     *
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone.replaceAll("[^0-9]", "");
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }
}
