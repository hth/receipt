package com.receiptofi.domain;

import com.receiptofi.utils.CommonUtil;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 10:16 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BIZ_STORE")
@CompoundIndexes (value = {
        /** Business name with address and phone makes it a unique store. */
        @CompoundIndex (name = "biz_store_idx", def = "{'AD': 1, 'PH': 1}", unique = true),
        @CompoundIndex (name = "biz_store_cor_cs_idx", def = "{'COR': '2d', 'CS': 1}"),
})
public class BizStoreEntity extends BaseEntity {

    /** Field name */
    public static final String ADDRESS_FIELD_NAME = "AD";
    public static final String PHONE_FIELD_NAME = "PH";

    /** Better to add a BLANK PHONE then to add nothing when biz does not have a phone number */
    @Value ("${phoneNumberBlank:000_000_0000}")
    private String phoneNumberBlank;

    @NotNull
    @Field ("AD")
    private String address;

    @NotNull
    @Field ("CS")
    private String countryShortName;

    @NotNull
    @Field ("PH")
    private String phone;

    @Field ("COR")
    private double[] coordinate;

    @Field ("PI")
    private String placeId;

    @Field ("PT")
    private String[] placeType;

    @Field ("PR")
    private float placeRating;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @Field ("EA")
    private boolean validatedUsingExternalAPI;

    public static BizStoreEntity newInstance() {
        return new BizStoreEntity();
    }

    /**
     * For web display of the address.
     *
     * @return
     */
    @Transient
    public String getAddressWrapped() {
        return address.replaceFirst(",", "<br/>");
    }

    @Transient
    public String getLocation() {
        String[] split = StringUtils.split(address, ",");
        return split[split.length - 3] + ", " + (split[split.length - 2]).trim().split(" ")[0];
    }

    public String getAddressWrappedMore() {
        return getAddressWrapped().replaceFirst(",", "<br/>");
    }

    /**
     * Escape String for Java Script.
     *
     * @return
     */
    public String getSafeJSAddress() {
        return StringEscapeUtils.escapeEcmaScript(address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = StringUtils.strip(address);
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Remove everything other than numbers. Do the formatting on client side.
     *
     * @param phone
     */
    public void setPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            this.phone = CommonUtil.phoneCleanup(phoneNumberBlank);
        } else {
            this.phone = CommonUtil.phoneCleanup(phone);
        }
    }

    public String getPhoneFormatted() {
        return CommonUtil.phoneFormatter(phone, countryShortName);
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public double getLat() {
        if (null != coordinate) {
            return coordinate[0];
        } else {
            return 0.0;
        }
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public double getLng() {
        if (null != coordinate) {
            return coordinate[1];
        } else {
            return 0.0;
        }
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public boolean isValidatedUsingExternalAPI() {
        return validatedUsingExternalAPI;
    }

    public void setValidatedUsingExternalAPI(boolean validatedUsingExternalAPI) {
        this.validatedUsingExternalAPI = validatedUsingExternalAPI;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String[] placeType) {
        this.placeType = placeType;
    }

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
    }
}
