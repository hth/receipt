package com.receiptofi.repository;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BillingHistoryEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(BillingHistoryEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object);
    }

    @Override
    public BillingHistoryEntity findOne(String id) {
        return null;
    }

    @Override
    public void deleteHard(BillingHistoryEntity object) {

    }

    @Override
    public BillingHistoryEntity findBillingHistoryForMonth(String billedForMonth, String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("BM").is(billedForMonth)),
                BillingHistoryEntity.class,
                TABLE);
    }

    @Override
    public List<BillingHistoryEntity> getHistory(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid))
                        .with(new Sort(DESC, "BM")),
                BillingHistoryEntity.class,
                TABLE);
    }
}
