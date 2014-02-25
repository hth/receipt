/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;
import org.bson.types.ObjectId;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

/**
 * @author hitender
 * @since Dec 24, 2012 3:19:22 PM
 *
 */
@Repository
public final class UserPreferenceManagerImpl implements UserPreferenceManager {
	private static final long serialVersionUID = -4805176857358849811L;

	private MongoTemplate mongoTemplate;

    @Autowired
    public UserPreferenceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
	public List<UserPreferenceEntity> getAllObjects() {
		return mongoTemplate.findAll(UserPreferenceEntity.class, TABLE);
	}

	@Override
	public void save(UserPreferenceEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
	}

	@Override
	public UserPreferenceEntity findOne(String id) {
		return mongoTemplate.findOne(query(where("id").is(new ObjectId(id))), UserPreferenceEntity.class, TABLE);
	}

	@Override
	public UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile) {
		return mongoTemplate.findOne(query(where("USER_PROFILE.$id").is(new ObjectId(userProfile.getId()))), UserPreferenceEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void deleteHard(UserPreferenceEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
