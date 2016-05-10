package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * User: hitender
 * Date: 5/9/16 2:18 PM
 */
@Document (collection = "COUPON_META")
@CompoundIndexes (value = {
        @CompoundIndex (name = "coupon_meta_idx", def = "{'RID': -1}", background = true)
})
public class CouponMetaEntity extends BaseEntity {

    @Field ("RID")
    private String rid;

    @Field ("FT")
    private String freeText;

    /** Period this coupon is valid from. */
    @Field ("AV")
    private Date available;

    /** Period this coupon is not valid. */
    @Field ("EX")
    private Date expire;

    /** Period this coupon is made available to world. */
    @Field ("RE")
    private Date release;

    @Field ("IP")
    private String imagePath;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @DBRef
    @Field ("BIZ_STORE")
    private BizStoreEntity bizStore;

    /** Number of coupon to release. */
    @Field ("CN")
    private int count;

    public String getFreeText() {
        return freeText;
    }

    public CouponMetaEntity setFreeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public Date getAvailable() {
        return available;
    }

    public CouponMetaEntity setAvailable(Date available) {
        this.available = available;
        return this;
    }

    public Date getExpire() {
        return expire;
    }

    public CouponMetaEntity setExpire(Date expire) {
        this.expire = expire;
        return this;
    }

    public Date getRelease() {
        return release;
    }

    public CouponMetaEntity setRelease(Date release) {
        this.release = release;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public CouponMetaEntity setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public CouponMetaEntity setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
        return this;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public CouponMetaEntity setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
        return this;
    }

    public int getCount() {
        return count;
    }

    public CouponMetaEntity setCount(int count) {
        this.count = count;
        return this;
    }
}
