/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
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

    /** When OptimisticLockingFailureException happen, ignore and re-create record. */
    private boolean ignoreOptimisticLockingFailureException = false;

    @Autowired
    public UserProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserProfileEntity object) {
        try {
            mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
            if (object.getId() != null) {
                if (!ObjectId.isValid(object.getId())) {
                    LOG.error("UserProfileId is not valid id={} rid={}", object.getId(), object.getReceiptUserId());
                }
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (OptimisticLockingFailureException e) {
            //TODO may be remove this condition in future. This is annoying temporary condition.
            if (ignoreOptimisticLockingFailureException) {
                /** This will re-create user profile with same details every time when there is a failure. */
                LOG.error("UserProfile saving optimistic locking failure, override optimistic locking rid={} reason={}",
                        object.getReceiptUserId(), e.getLocalizedMessage(), e);

                WriteResult writeResult = mongoTemplate.remove(
                        query(where("RID").is(object.getReceiptUserId())),
                        UserProfileEntity.class,
                        TABLE);
                if (writeResult.getN() > 0) {
                    LOG.info("Deleted optimistic locking data issue for rid={}", object.getReceiptUserId());
                    object.setId(null);
                    object.setVersion(null);
                    mongoTemplate.save(object, TABLE);
                } else {
                    LOG.error("Delete failed on locking issue for rid={}", object.getReceiptUserId());
                    throw e;
                }
            } else {
                throw e;
            }
        } catch (DataIntegrityViolationException e) {
            LOG.error("Found existing userProfile rid={} email={}", object.getReceiptUserId(), object.getEmail());
            throw e;
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
    public UserProfileEntity findByReceiptUserId(String rid) {
        return mongoTemplate.findOne(byReceiptUserId(rid, true), UserProfileEntity.class, TABLE);
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
     * Find any user matching with provider user id; ignore active or not active profile.
     *
     * @param puid unique string from Google or Facebook
     * @return
     */
    @Override
    public UserProfileEntity findByProviderUserId(String puid) {
        return mongoTemplate.findOne(query(where("PUID").is(puid)), UserProfileEntity.class, TABLE);
    }

    /**
     * Find any user matching with provider user id and email; ignore active or not active profile.
     *
     * @param puid  unique string from Google or Facebook
     * @param email unique email address
     * @return
     */
    @Override
    public UserProfileEntity findByProviderUserIdOrEmail(String puid, String email) {
        Assert.hasLength(email, "Email should not be empty");
        return mongoTemplate.findOne(
                query(new Criteria()
                        .orOperator(
                                where("PUID").is(puid),
                                where("EM").is(email))),
                UserProfileEntity.class,
                TABLE);
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
        return mongoTemplate.find(
                query(new Criteria()
                                .orOperator(
                                        where("FN").regex(name, "i"),
                                        where("LN").regex(name, "i")
                                )
                ),
                UserProfileEntity.class, TABLE
        );
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

    @Override
    public void updateCountryShortName(String countryShortName, String rid) {
        mongoTemplate.updateFirst(
                query(where("RID").is(rid)),
                entityUpdate(update("CS", countryShortName)),
                UserProfileEntity.class,
                TABLE
        );
    }
}
