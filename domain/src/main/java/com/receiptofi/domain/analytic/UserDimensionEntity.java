package com.receiptofi.domain.analytic;

import com.receiptofi.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 7/2/16 2:59 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "Z_USER_DIM")
@CompoundIndexes (value = {
        @CompoundIndex (name = "user_dim_idx", def = "{'storeId': 1}", background = true),
        @CompoundIndex (name = "user_dim_biz_idx", def = "{'bizId': 1}", background = true),
        @CompoundIndex (name = "user_dim_geo_idx", def = "{'COR': '2dsphere'}", background = true),
})
public class UserDimensionEntity extends BaseEntity {

    @Field ("RID")
    private String rid;

    @Field ("storeId")
    private String storeId;

    //TODO add
    @Field ("bizId")
    private String bizId;

    @Field ("maxVisit")
    private long maxVisit;

    @Field ("COR")
    private double[] coordinate;

    public String getRid() {
        return rid;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getBizId() {
        return bizId;
    }

    public long getMaxVisit() {
        return maxVisit;
    }

    public double[] getCoordinate() {
        return coordinate;
    }
}
