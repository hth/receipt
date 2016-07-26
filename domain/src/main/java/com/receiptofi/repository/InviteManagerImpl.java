package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:15 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class InviteManagerImpl implements InviteManager {
    private static final Logger LOG = LoggerFactory.getLogger(InviteManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            InviteEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public InviteManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public InviteEntity findByAuthenticationKey(String auth) {
        return mongoTemplate.findOne(
                query(where("AU").is(auth).andOperator(
                        isActive(),
                        isNotDeleted())
                ),
                InviteEntity.class,
                TABLE);
    }

    @Override
    public void invalidateAllEntries(InviteEntity invite) {
        mongoTemplate.updateMulti(
                query(where("IN.$id").is(new ObjectId(invite.getInvited().getId()))),
                entityUpdate(update("A", false)),
                InviteEntity.class
        );
    }

    @Override
    public void save(InviteEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(InviteEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public InviteEntity reInviteActiveInvite(String emailId, UserAccountEntity invitedBy) {
        return mongoTemplate.findOne(
                query(where("EM").is(emailId)
                        .and("INV.$id").is(new ObjectId(invitedBy.getId()))
                        .andOperator(isNotDeleted())),
                InviteEntity.class,
                TABLE);
    }

    @Override
    public InviteEntity find(String emailId) {
        return mongoTemplate.findOne(
                query(where("EM").is(emailId).andOperator(
                        isActive(),
                        isNotDeleted())
                ),
                InviteEntity.class,
                TABLE);
    }
}
