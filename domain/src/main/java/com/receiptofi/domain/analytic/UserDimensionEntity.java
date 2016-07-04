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
        @CompoundIndex (name = "user_dim_idx", def = "{'storeId': 1}", unique = false, background = true)
})
public class UserDimensionEntity extends BaseEntity {

    @Field ("RID")
    String rid;

    @Field ("storeId")
    private String storeId;

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

    public long getMaxVisit() {
        return maxVisit;
    }

    public double[] getCoordinate() {
        return coordinate;
    }
}
