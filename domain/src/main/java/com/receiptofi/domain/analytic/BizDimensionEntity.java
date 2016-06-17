package com.receiptofi.domain.analytic;

import com.receiptofi.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 6/8/16 3:47 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "Z_BIZ_DIM")
@CompoundIndexes (value = {
        @CompoundIndex (name = "biz_dim_idx", def = "{'bizId': 1}", unique = true, background = true)
})
public class BizDimensionEntity extends BaseEntity {

    @Field ("bizName")
    private String bizName;

    @Field ("bizId")
    private String bizId;

    @Field ("userCount")
    private long userCount;

    @Field ("storeCount")
    private long storeCount;

    @Field ("bizTotal")
    private Double bizTotal;

    @Field ("visitCnt")
    private long visitCount;

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getStoreCount() {
        return storeCount;
    }

    public void setStoreCount(long storeCount) {
        this.storeCount = storeCount;
    }

    public Double getBizTotal() {
        return bizTotal;
    }

    public void setBizTotal(Double bizTotal) {
        this.bizTotal = bizTotal;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(long visitCount) {
        this.visitCount = visitCount;
    }
}
