package com.receiptofi.domain;

import com.receiptofi.domain.types.CouponTypeEnum;
import com.receiptofi.domain.types.CouponUploadStatusEnum;
import com.receiptofi.domain.util.ImagePathOnS3;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 4/27/16 11:59 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "COUPON")
@CompoundIndexes (value = {
        @CompoundIndex (name = "coupon_idx", def = "{'RID': -1}", background = true),
        @CompoundIndex (name = "coupon_rid_initiated_idx", def = "{'RID': -1, 'IF': -1}", unique = true, background = true),
        @CompoundIndex (name = "coupon_initiated_idx", def = "{'IF': -1}", unique = false, background = true)
})
public class CouponEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String rid;

    /** User generated coupon local Id */
    @Field ("LID")
    private String localId;

    @Field ("BN")
    private String businessName;

    @Field ("FT")
    private String freeText;

    /** Period this coupon is valid from. */
    @Field ("AV")
    private Date available;

    /** Period this coupon is not valid after. */
    @Field ("EX")
    private Date expire;

    @Field ("CT")
    private CouponTypeEnum couponType = CouponTypeEnum.I;

    @Field ("RM")
    private boolean reminder = false;

    /** Saved local image location when coupon FileSystemEntity is null. */
    @Field ("IP")
    private String imagePath;

    /** All coupons are saved on cloud. Shared coupons refers to same image. */
    @Field ("SH")
    private List<String> sharedWithRids = new ArrayList<>();

    /**
     * Holds the id where this coupon has originated from. For business coupon, this is blank and
     * for individual, the owner has a blank originId.
     */
    @Field ("OI")
    private String originId;

    /**
     * Holds the id where it originate from originally. For business coupon, this is BusinessCampaignId and
     * for individual, the owner has a id as initiatedFromId but rest of the shared coupon would have the same id
     * where the coupon had originated from. This will help in creating just one coupon per campaign.
     * Or Avoid creating multiple coupons on sharing.
     */
    @Field ("IF")
    private String initiatedFromId;

    @Field ("UC")
    private boolean usedCoupon;

    @DBRef
    @Field ("FS")
    private Collection<FileSystemEntity> fileSystemEntities;

    @Field ("CU")
    private CouponUploadStatusEnum couponUploadStatus = CouponUploadStatusEnum.I;

    public String getLocalId() {
        return localId;
    }

    public CouponEntity setLocalId(String localId) {
        this.localId = localId;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public CouponEntity setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getFreeText() {
        return freeText;
    }

    public CouponEntity setFreeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public Date getAvailable() {
        return available;
    }

    public CouponEntity setAvailable(Date available) {
        this.available = available;
        return this;
    }

    public Date getExpire() {
        return expire;
    }

    public CouponEntity setExpire(Date expire) {
        this.expire = expire;
        return this;
    }

    public CouponTypeEnum getCouponType() {
        return couponType;
    }

    public CouponEntity setCouponType(CouponTypeEnum couponType) {
        this.couponType = couponType;
        return this;
    }

    public boolean isReminder() {
        return reminder;
    }

    public CouponEntity setReminder(boolean reminder) {
        this.reminder = reminder;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public CouponEntity setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getRid() {
        return rid;
    }

    public CouponEntity setRid(String rid) {
        this.rid = rid;
        return this;
    }

    public boolean isUsedCoupon() {
        return usedCoupon;
    }

    public CouponEntity setUsedCoupon(boolean usedCoupon) {
        this.usedCoupon = usedCoupon;
        return this;
    }

    public List<String> getSharedWithRids() {
        return sharedWithRids;
    }

    public CouponEntity setSharedWithRids(List<String> sharedWithRids) {
        this.sharedWithRids = sharedWithRids;
        return this;
    }

    public String getOriginId() {
        return originId;
    }

    public CouponEntity setOriginId(String originId) {
        this.originId = originId;
        return this;
    }

    public String getInitiatedFromId() {
        return initiatedFromId;
    }

    public CouponEntity setInitiatedFromId(String initiatedFromId) {
        this.initiatedFromId = initiatedFromId;
        return this;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public CouponEntity setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
        this.couponUploadStatus = CouponUploadStatusEnum.A;
        this.imagePath = ImagePathOnS3.populateImagePath(fileSystemEntities);
        return this;
    }

    public CouponUploadStatusEnum getCouponUploadStatus() {
        return couponUploadStatus;
    }

    public CouponEntity setCouponUploadStatus(CouponUploadStatusEnum couponUploadStatus) {
        this.couponUploadStatus = couponUploadStatus;
        return this;
    }

    @Override
    public String toString() {
        return "CouponEntity{" +
                "id=" + id +
                ", rid='" + rid + '\'' +
                ", localId='" + localId + '\'' +
                ", businessName='" + businessName + '\'' +
                ", freeText='" + freeText + '\'' +
                ", available=" + available +
                ", expire=" + expire +
                ", couponType=" + couponType +
                ", reminder=" + reminder +
                ", imagePath='" + imagePath + '\'' +
                ", sharedWithRids=" + sharedWithRids +
                ", originId='" + originId + '\'' +
                ", initiatedFromId='" + initiatedFromId + '\'' +
                ", usedCoupon=" + usedCoupon +
                ", fileSystemEntities=" + fileSystemEntities +
                ", couponUploadStatus=" + couponUploadStatus +
                '}';
    }
}
