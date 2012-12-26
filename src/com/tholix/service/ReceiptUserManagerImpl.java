/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.stereotype.Repository;

import com.mongodb.CommandResult;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.tholix.domain.ReceiptUserEntity;

/**
 * @author hitender 
 * @when Dec 16, 2012 1:20:53 PM
 */
@Repository
public class ReceiptUserManagerImpl implements ReceiptUserManager {
	private final Log log = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 5745317401200234475L;	
	
	@Autowired
    MongoTemplate mongoTemplate;
	
	@Override
	public List<ReceiptUserEntity> getAllObjects() {
		return mongoTemplate.findAll(ReceiptUserEntity.class);
	}

	@Override
	public void saveObject(ReceiptUserEntity receiptUser) throws Exception {
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		try {
			mongoTemplate.insert(receiptUser, TABLE);
		} catch (DataIntegrityViolationException e) {
			log.error("Duplicate record entry: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public ReceiptUserEntity getObject(String emailId) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("emailId").is(emailId)), 
				ReceiptUserEntity.class, 
				TABLE);
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		return mongoTemplate.updateFirst(
				new Query(Criteria.where("id").is(id)),
				Update.update("name", name), 
				TABLE);
	}

	@Override
	public void deleteObject(String id) {
		mongoTemplate.remove(
				new Query(Criteria.where("id").is(id)), 
				TABLE);		
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
