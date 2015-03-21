package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.BillingHistoryEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 3/19/15 2:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BillingAccountManagerImpl implements BillingAccountManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BillingAccountEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(BillingAccountEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object);
    }

    @Override
    public BillingAccountEntity findOne(String id) {
        return null;
    }

    @Override
    public void deleteHard(BillingAccountEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BillingAccountEntity getBillingAccount(String rid) {
        return mongoTemplate.findOne(query(where("RID").is(rid)), BillingAccountEntity.class);
    }
}
