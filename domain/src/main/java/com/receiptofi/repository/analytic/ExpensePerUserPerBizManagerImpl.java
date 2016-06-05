package com.receiptofi.repository.analytic;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.previousOperation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.ExpensePerUserPerBizEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 6/3/16 6:21 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ExpensePerUserPerBizManagerImpl implements ExpensePerUserPerBizManager {
    private static final Logger LOG = LoggerFactory.getLogger(ExpensePerUserPerBizManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ExpensePerUserPerBizEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ExpensePerUserPerBizManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ExpensePerUserPerBizEntity> getTotalCustomerPurchases(String bizId) {
        TypedAggregation<ExpensePerUserPerBizEntity> agg = newAggregation(ExpensePerUserPerBizEntity.class,
                match(where("group::bizId").is(bizId)),
                group("bizId")
                        .sum("bizTotal").as("bizTotal"),
                sort(DESC, previousOperation())
        );

        return mongoTemplate.aggregate(agg, TABLE, ExpensePerUserPerBizEntity.class).getMappedResults();
    }

    @Override
    public void save(ExpensePerUserPerBizEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void deleteHard(ExpensePerUserPerBizEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
