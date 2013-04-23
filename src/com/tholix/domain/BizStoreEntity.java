package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.NumberFormat;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 10:16 PM
 */

@Document(collection = "BIZ_STORE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "biz_store_idx", def = "{'address': 1, 'phone': 1}", unique=true),
} )
public class BizStoreEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 128)
    private String address;

    @NotNull
    @Size(min = 0, max = 20)
    private String phone;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private double lat;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private double lng;

    @DBRef
    private BizNameEntity bizName;

    /* To make bean happy */
    public BizStoreEntity() {

    }

    public static BizStoreEntity newInstance() {
        return new BizStoreEntity();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
