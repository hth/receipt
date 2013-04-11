/**
 *
 */
package com.tholix.service;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @when Dec 23, 2012 3:45:47 AM
 *
 */
public class UserProfileManagerImpl implements UserProfileManager {
	private static final long serialVersionUID = 7078530488197339683L;
	private static final Logger log = Logger.getLogger(UserProfileManagerImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<UserProfileEntity> getAllObjects() {
		return mongoTemplate.findAll(UserProfileEntity.class, TABLE);
	}

	@Override
	public void save(UserProfileEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
		try {
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for UserProfileEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object) {
		return mongoTemplate.findOne(new Query(Criteria.where("userAuthentication").is(object)), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity getObjectUsingEmail(String emailId) {
		return mongoTemplate.findOne(new Query(Criteria.where("emailId").is(emailId)), UserProfileEntity.class, TABLE);
	}

	@Override
	public UserProfileEntity findOne(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(new ObjectId(id))), UserProfileEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// return mongoTemplate.updateFirst(
		// new Query(Criteria.where("id").is(id)),
		// Update.update("level", name), TABLE);
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public WriteResult updateObject(String id, UserLevelEnum level) {
		return mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(id))), Update.update("level", level), TABLE);
	}

	@Override
	public void delete(UserProfileEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}
	}

	@Override
	public List<UserProfileEntity> searchByName(String name) {
		//TODO look into PageRequest for limit data
		//PageRequest request = new PageRequest(0, 1, new Sort("created", Directions.DESC));

		//TODO this does not seems to be working query
		Criteria a = Criteria.where("firstName").regex(name, "i");
		//Criteria b = Criteria.where("lastName").regex(name, "i");
		//return mongoTemplate.find(new Query(a.orOperator(b)), UserProfileEntity.class, TABLE);
		return mongoTemplate.find(new Query(a), UserProfileEntity.class, TABLE);
	}

    @Override
    public UserProfileEntity searchByEmail(String emailId) {
        Criteria a = Criteria.where("emailId").is(emailId);
        return mongoTemplate.findOne(new Query(a), UserProfileEntity.class, TABLE);
    }
}
