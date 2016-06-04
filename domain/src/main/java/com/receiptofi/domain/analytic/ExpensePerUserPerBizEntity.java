package com.receiptofi.domain.analytic;

import com.receiptofi.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 6/3/16 5:54 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "A_EXPENSE_PER_USER_PER_BIZ")
@CompoundIndexes (value = {
        @CompoundIndex (name = "expense_per_user_per_biz_idx", def = "{'group::bizId': 1}", background = true)
})
public class ExpensePerUserPerBizEntity extends BaseEntity {

    @Field ("group::rid")
    private String rid;

    @Field ("group::bizId")
    private String bizId;

    @Field ("bizTotal")
    private Double bizTotal;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Double getBizTotal() {
        return bizTotal;
    }

    public void setBizTotal(Double bizTotal) {
        this.bizTotal = bizTotal;
    }
}
