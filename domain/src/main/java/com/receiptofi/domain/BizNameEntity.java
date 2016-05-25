package com.receiptofi.domain;

import com.receiptofi.domain.types.BusinessTypeEnum;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

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
@Document (collection = "BIZ_NAME")
@CompoundIndexes (value = {
        @CompoundIndex (name = "biz_name_idx", def = "{'N': 1}", unique = false),
})
public class BizNameEntity extends BaseEntity {

    @NotNull
    @Field ("N")
    private String businessName;

    @Field ("BT")
    private List<BusinessTypeEnum> businessTypes = new ArrayList<>();

    public static BizNameEntity newInstance() {
        return new BizNameEntity();
    }

    public String getBusinessName() {
        return businessName;
    }

    /**
     * Cannot: Added Capitalize Fully feature to business businessName as the businessName has to be matching with
     * business style.
     *
     * @param businessName
     */
    public void setBusinessName(String businessName) {
        this.businessName = WordUtils.capitalizeFully(StringUtils.trim(businessName));
    }

    public List<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    @SuppressWarnings("unused")
    public void setBusinessTypes(List<BusinessTypeEnum> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public void addBusinessType(BusinessTypeEnum businessType) {
        this.businessTypes.add(businessType);
    }

    /**
     * Escape String for Java Script.
     *
     * @return
     */
    public String getSafeJSBusinessName() {
        return StringEscapeUtils.escapeEcmaScript(businessName);
    }

    @Override
    public String toString() {
        return "BizNameEntity{" +
                "businessName='" + businessName + '\'' +
                '}';
    }
}
