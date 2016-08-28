package com.receiptofi.domain.analytic;

import com.receiptofi.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 7/2/16 2:58 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "Z_STORE_DIM")
@CompoundIndexes (value = {
        @CompoundIndex (name = "store_dim_idx", def = "{'bizId': 1}", background = true),
        @CompoundIndex (name = "store_dim_geo_idx", def = "{'COR': '2dsphere'}", background = true),
})
public class StoreDimensionEntity extends BaseEntity {

    @Field ("bizId")
    private String bizId;

    @Field ("bizName")
    private String bizName;

    @Field ("storeId")
    private String storeId;

    @Field ("storeAdd")
    private String storeAdd;

    @Field ("storeVisits")
    private long storeVisits;

    @Field ("storeTot")
    private Double storeTot;

    @Field ("COR")
    private double[] coordinate;

    public String getBizId() {
        return bizId;
    }

    public String getBizName() {
        return bizName;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreAdd() {
        return storeAdd;
    }

    public long getStoreVisits() {
        return storeVisits;
    }

    public Double getStoreTot() {
        return storeTot;
    }

    public double[] getCoordinate() {
        return coordinate;
    }
}
