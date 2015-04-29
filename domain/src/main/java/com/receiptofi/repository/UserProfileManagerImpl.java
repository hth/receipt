/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * @author hitender
 * @since Dec 23, 2012 3:45:47 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class UserProfileManagerImpl implements UserProfileManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserProfileEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserProfileEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
//            if (findByEmail(object.getEmailId()) == null)
//                mongoTemplate.save(object, TABLE);
//            else {
//                LOG.error("User seems to be already registered: " + object.getEmailId());
//                throw new Exception("User already registered with email: " + object.getEmailId());
//            }
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserProfileEntity={}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object) {
        return mongoTemplate.findOne(
                query(where("USER_AUTHENTICATION.$id").is(new ObjectId(object.getId()))),
                UserProfileEntity.class,
                TABLE);
    }

    /**
     * Find any user matching with email; ignore active or not active
     *
     * @param email
     * @return
     */
    @Override
    public UserProfileEntity findByEmail(String email) {
        return mongoTemplate.findOne(query(where("EM").is(email)), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity findByReceiptUserId(String receiptUserId) {
        return mongoTemplate.findOne(byReceiptUserId(receiptUserId, true), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity forProfilePreferenceFindByReceiptUserId(String rid) {
        return mongoTemplate.findOne(byReceiptUserId(rid, false), UserProfileEntity.class, TABLE);
    }

    private Query byReceiptUserId(String receiptUserId, boolean activeProfile) {
        if (activeProfile) {
            return query(where("RID").is(receiptUserId).andOperator(isActive()));
        } else {
            return query(where("RID").is(receiptUserId));
        }
    }

    /**
     * Find any user matching with email; ignore active or not active
     *
     * @param uid
     * @return
     */
    @Override
    public UserProfileEntity findByUserId(String uid) {
        return mongoTemplate.findOne(query(where("UID").is(uid)), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id)), UserProfileEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserProfileEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<UserProfileEntity> searchAllByName(String name) {
        //TODO look into PageRequest for limit data
        //PageRequest request = new PageRequest(0, 1, new Sort("created", Directions.DESC));

        //Can add "^" + to force search only the names starting with
        Criteria a = where("FN").regex(name, "i");
        Criteria b = where("LN").regex(name, "i");
        return mongoTemplate.find(query(new Criteria().orOperator(a, b)), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity findOneByMail(String mail) {
        return mongoTemplate.findOne(query(where("EM").is(mail)), UserProfileEntity.class, TABLE);
    }

    @Override
    public UserProfileEntity getProfileUpdateSince(String rid, Date since) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("U").gte(since)),
                UserProfileEntity.class,
                TABLE
        );
    }
}
