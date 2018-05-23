/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author hitender
 * @since Dec 24, 2012 3:19:22 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class UserPreferenceManagerImpl implements UserPreferenceManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserPreferenceEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserPreferenceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserPreferenceEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public UserPreferenceEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(new ObjectId(id))), UserPreferenceEntity.class, TABLE);
    }

    @Override
    public UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile) {
        return mongoTemplate.findOne(query(where("USER_PROFILE.$id").is(new ObjectId(userProfile.getId()))),
                UserPreferenceEntity.class,
                TABLE);
    }

    @Override
    public UserPreferenceEntity getByRid(String rid) {
        return mongoTemplate.findOne(query(where("RID").is(rid)), UserPreferenceEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserPreferenceEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<UserPreferenceEntity> getAll() {
        return mongoTemplate.findAll(UserPreferenceEntity.class);
    }
}
