package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.BillingPlanEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 3/19/15 2:53 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BillingHistoryManagerImpl implements BillingHistoryManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BillingHistoryEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BillingHistoryManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BillingHistoryEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(BillingHistoryEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BillingHistoryEntity findLatestBillingHistoryForMonth(String billedForMonth, String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("BM").is(billedForMonth)).with(new Sort(DESC, "U")),
                BillingHistoryEntity.class,
                TABLE);
    }

    @Override
    public List<BillingHistoryEntity> getHistory(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(new Order(DESC, "BM"), new Order(DESC, "U"))),
                BillingHistoryEntity.class);
    }

    @Override
    public long countLastPromotion(Date thisMonth, String rid) {
        LocalDateTime res = LocalDateTime.ofInstant(Instant.ofEpochMilli(thisMonth.getTime()), ZoneId.systemDefault());
        Date previousMonth = Date.from(res.minusMonths(1).toInstant(ZoneOffset.UTC));

        return mongoTemplate.count(
                query(
                        where("RID").is(rid)
                                .and("BS").is(BilledStatusEnum.P)
                                .orOperator(
                                        where("BM").is(BillingHistoryEntity.YYYY_MM.format(previousMonth)),
                                        where("BM").is(BillingHistoryEntity.YYYY_MM.format(thisMonth))
                                )
                ),
                BillingHistoryEntity.class
        );
    }

    @Override
    public long countBillingHistory(String billedForMonth, BillingPlanEnum billingPlan) {
        return mongoTemplate.count(query(where("BS").ne(billingPlan).and("BM").is(billedForMonth)), BillingHistoryEntity.class);
    }
}
