/**
 *
 */
package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAuthenticationEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hitender
 * @since Dec 16, 2012 1:20:53 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@Repository
public final class UserAuthenticationManagerImpl implements UserAuthenticationManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserAuthenticationEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserAuthenticationManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserAuthenticationEntity> getAllObjects() {
        return mongoTemplate.findAll(UserAuthenticationEntity.class, TABLE);
    }

    @Override
    public void save(UserAuthenticationEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAuthenticationEntity:{} {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserAuthenticationEntity findOne(String id) {
        return mongoTemplate.findOne(query(Criteria.where("id").is(id)), UserAuthenticationEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserAuthenticationEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
