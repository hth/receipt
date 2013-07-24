package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 10:16 PM
 */
@Document(collection = "BIZ_NAME")
@CompoundIndexes(value = {
        @CompoundIndex(name = "biz_name_idx", def = "{'NAME': 1}",  unique = true),
} )
public class BizNameEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 60)
    @Field("NAME")
    private String name;

    /* To make bean happy */
    public BizNameEntity() {}

    public static BizNameEntity newInstance() {
        return new BizNameEntity();
    }

    public String getName() {
        return name;
    }

    /**
     * Added Capitalize Fully feature to business name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(StringUtils.strip(name));
    }
}
