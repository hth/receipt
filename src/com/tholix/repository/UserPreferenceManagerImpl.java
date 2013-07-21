/**
 *
 */
package com.tholix.repository;

import org.bson.types.ObjectId;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * @author hitender
 * @since Dec 24, 2012 3:19:22 PM
 *
 */
@Repository
@Transactional(readOnly = true)
public final class UserPreferenceManagerImpl implements UserPreferenceManager {
	private static final long serialVersionUID = -4805176857358849811L;

	@Autowired private MongoTemplate mongoTemplate;

	@Override
	public List<UserPreferenceEntity> getAllObjects() {
		return mongoTemplate.findAll(UserPreferenceEntity.class, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(UserPreferenceEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		mongoTemplate.save(object, TABLE);
	}

	@Override
	public UserPreferenceEntity findOne(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("id").is(new ObjectId(id))), UserPreferenceEntity.class, TABLE);
	}

	@Override
	public UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile) {
		return mongoTemplate.findOne(Query.query(Criteria.where("USER_PROFILE.$id").is(new ObjectId(userProfile.getId()))), UserPreferenceEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteHard(UserPreferenceEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}
	}

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
