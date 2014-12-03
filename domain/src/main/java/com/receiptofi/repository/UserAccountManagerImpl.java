package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAccountEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 4/23/14 6:43 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class UserAccountManagerImpl implements UserAccountManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserAccountEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserAccountManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserAccountEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserAccountEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), UserAccountEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserAccountEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public UserAccountEntity findByReceiptUserId(String rid) {
        return mongoTemplate.findOne(query(where("RID").is(rid)), UserAccountEntity.class, TABLE);
    }

    @Override
    public UserAccountEntity findByUserId(String mail) {
        return mongoTemplate.findOne(query(where("UID").is(mail)), UserAccountEntity.class, TABLE);
    }

    @Override
    public UserAccountEntity findByProviderUserId(String providerUserId) {
        return mongoTemplate.findOne(query(where("PUID").is(providerUserId)), UserAccountEntity.class, TABLE);
    }
}
