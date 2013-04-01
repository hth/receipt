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
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;
import com.tholix.domain.UserAuthenticationEntity;

/**
 * @author hitender
 * @when Dec 16, 2012 1:20:53 PM
 */
@Repository
public class UserAuthenticationManagerImpl implements UserAuthenticationManager {
	private static final Logger log = Logger.getLogger(UserAuthenticationManagerImpl.class);

	private static final long serialVersionUID = 5745317401200234475L;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<UserAuthenticationEntity> getAllObjects() {
		return mongoTemplate.findAll(UserAuthenticationEntity.class, TABLE);
	}

	@Override
	public void saveObject(UserAuthenticationEntity object) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			mongoTemplate.save(object, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry for UserAuthenticationEntity: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public UserAuthenticationEntity getObject(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), UserAuthenticationEntity.class, TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		return mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), Update.update("name", name), TABLE);
	}

	@Override
	public void deleteObject(UserAuthenticationEntity object) {
		mongoTemplate.remove(object, TABLE);
	}

	@Override
	public void createCollection() {
		if (!mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.createCollection(TABLE);
		}

	}

	@Override
	public void dropCollection() {
		if (mongoTemplate.collectionExists(TABLE)) {
			mongoTemplate.dropCollection(TABLE);
		}
	}
}
