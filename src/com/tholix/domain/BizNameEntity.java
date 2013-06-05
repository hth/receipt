package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 10:16 PM
 */
@Document(collection = "BIZ_NAME")
@CompoundIndexes(value = {
        @CompoundIndex(name = "biz_name_idx", def = "{'name': 1}",  unique = true),
} )
public class BizNameEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 60)
    private String name;

    /* To make bean happy */
    public BizNameEntity() {}

    public static BizNameEntity newInstance() {
        return new BizNameEntity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
