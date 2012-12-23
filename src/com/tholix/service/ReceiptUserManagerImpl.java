/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;
import com.tholix.domain.ReceiptUser;

/**
 * @author hitender 
 * @when Dec 16, 2012 1:20:53 PM
 */
@Repository
public class ReceiptUserManagerImpl implements ReceiptUserManager {
	protected final Log logger = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 5745317401200234475L;	
	
	@Autowired
    MongoTemplate mongoTemplate;
	
	@Override
	public List<ReceiptUser> getAllObjects() {
		return mongoTemplate.findAll(ReceiptUser.class);
	}

	@Override
	public void saveObject(ReceiptUser receiptUser) {
		mongoTemplate.insert(receiptUser, TABLE);
	}
	
	@Override
	public ReceiptUser getObject(String emailId) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("emailId").is(emailId)), 
				ReceiptUser.class, 
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
