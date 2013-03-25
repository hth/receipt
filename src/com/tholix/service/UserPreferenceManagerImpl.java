/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.WriteResult;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * @author hitender
 * @when Dec 24, 2012 3:19:22 PM
 * 
 */
public class UserPreferenceManagerImpl implements UserPreferenceManager {
	private static final long serialVersionUID = -4805176857358849811L;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<UserPreferenceEntity> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(UserPreferenceEntity object) {
		mongoTemplate.save(object, TABLE);
	}

	@Override
	public UserPreferenceEntity getObject(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(new ObjectId(id))), UserPreferenceEntity.class, TABLE);
	}
	
	@Override
	public UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile) {
		return mongoTemplate.findOne(new Query(Criteria.where("userProfile").is(userProfile)), UserPreferenceEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String id) {
		mongoTemplate.remove(new Query(Criteria.where("id").is(new ObjectId(id))), TABLE);
	}

	@Override
	public void createCollection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}
	}

}
