package com.receiptofi.domain;

import com.receiptofi.domain.value.Coordinate;
import com.receiptofi.utils.Formatter;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
        @CompoundIndex (name = "biz_store_idx", def = "{'AD': 1, 'PH': 1}", unique = true),
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
    @Field ("PH")
    private String phone;

    @Field ("COR")
    private Coordinate coordinate;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @Field ("EA")
    private boolean validatedUsingExternalAPI;

    public static BizStoreEntity newInstance() {
        return new BizStoreEntity();
    }

    /**
     * Strip all the characters other than number.
     *
     * @param phone
     * @return
     */
    public static String phoneCleanup(String phone) {
        if (StringUtils.isNotEmpty(phone)) {
            return phone.replaceAll("[^0-9]", StringUtils.EMPTY);
        }
        return phone;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = StringUtils.strip(address);
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
        if (StringUtils.isEmpty(phone)) {
            this.phone = phoneCleanup(phoneNumberBlank);
        } else {
            this.phone = phoneCleanup(phone);
        }
    }

    public String getPhoneFormatted() {
        return Formatter.phone(phone);
    }

    public double getLat() {
        return coordinate.getLat();
    }

    public double getLng() {
        return coordinate.getLng();
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

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }


}
