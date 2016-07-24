package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ExpenseTallyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 7/23/16 9:21 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ExpenseTallyManagerImpl implements ExpenseTallyManager {
    private static final Logger LOG = LoggerFactory.getLogger(ExpenseTagManagerImpl.class);
    public static final String TABLE = BaseEntity.getClassAnnotationValue(
            ExpenseTallyEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ExpenseTallyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void save(ExpenseTallyEntity object) {

    }

    @Override
    public void deleteHard(ExpenseTallyEntity object) {

    }

    @Override
    public List<ExpenseTallyEntity> getUsersForExpenseTally(String tid, int limit) {
        return mongoTemplate.find(
                query(where("TID").is(tid).and("CON").is(true)).limit(limit),
                ExpenseTallyEntity.class,
                TABLE);
    }
}
