/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.WriteResult;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * @author hitender
 * @when Dec 23, 2012 3:45:47 AM
 * 
 */
public class UserProfileManagerImpl implements UserProfileManager {
	private static final long serialVersionUID = 7078530488197339683L;
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<UserProfileEntity> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(UserProfileEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for UserProfileEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public UserProfileEntity getObject(UserAuthenticationEntity object) {
		return mongoTemplate.findOne(new Query(Criteria.where("userAuthentication").is(object)), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity getObjectUsingEmail(String emailId) {
		return mongoTemplate.findOne(new Query(Criteria.where("emailId").is(emailId)), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity getObject(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), UserProfileEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String id) {
		// TODO Auto-generated method stub

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
